<?php
$host = 'localhost';
$dbname = 'forage';
$user = 'root';
$pass = '';

function e($value) {
    return htmlspecialchars((string) $value, ENT_QUOTES, 'UTF-8');
}

function formatHours(int $minutes): string {
    if ($minutes <= 0) {
        return '0';
    }
    $hours = $minutes / 60;
    return number_format($hours, 2, ',', ' ') . ' h';
}

function loadSuiviInfo(PDO $pdo, int $idDemande): array {
    $statutStmt = $pdo->prepare(
        'SELECT ds.id_statut, ds.duree_travail, ds.duree_total, ds.description, ds.date '
        . 'FROM demande_statut ds '
        . 'WHERE ds.id_demande = ? '
        . 'ORDER BY ds.date ASC, ds.id ASC'
    );
    $statutStmt->execute([$idDemande]);
    $statuts = $statutStmt->fetchAll(PDO::FETCH_ASSOC);

    $paramsStmt = $pdo->query('SELECT idStatut1, idStatut2, duree_min, duree_max, alerte_couleur FROM parametres');
    $params = [];
    foreach ($paramsStmt->fetchAll(PDO::FETCH_ASSOC) as $row) {
        $id1 = (int) $row['idStatut1'];
        $id2 = (int) $row['idStatut2'];
        $params[$id1][$id2][] = [
            'min' => (int) $row['duree_min'],
            'max' => (int) $row['duree_max'],
            'couleur' => trim((string) $row['alerte_couleur'])
        ];
    }

    foreach ($params as $id1 => $pairs) {
        foreach ($pairs as $id2 => $values) {
            usort($values, function ($a, $b) {
                return $a['min'] <=> $b['min'];
            });
            $params[$id1][$id2] = $values;
        }
    }

    $paramsByEnd = [];
    foreach ($params as $id1 => $pairs) {
        foreach ($pairs as $id2 => $values) {
            $paramsByEnd[$id2][$id1] = $values;
        }
    }

    $labels = [];
    $seen = [];
    $totalMinutes = 0;
    $count = count($statuts);
    for ($i = 0; $i < $count; $i++) {
        $currentStatut = (int) $statuts[$i]['id_statut'];
        if ($currentStatut === 8) {
            $totalMinutes = isset($statuts[$i]['duree_total']) ? (int) $statuts[$i]['duree_total'] : 0;
        }
        if (!isset($paramsByEnd[$currentStatut]) || $i <= 0) {
            continue;
        }

        $durationsByStart = [];
        $sum = 0;
        for ($j = $i - 1; $j >= 0; $j--) {
            $nextDuration = isset($statuts[$j + 1]['duree_travail']) ? (int) $statuts[$j + 1]['duree_travail'] : 0;
            $sum += $nextDuration;
            $startId = (int) $statuts[$j]['id_statut'];
            if (!isset($durationsByStart[$startId])) {
                $durationsByStart[$startId] = $sum;
            }
        }

        $bestStart = null;
        $bestDuration = null;
        $bestColor = 'aucun';
        foreach ($paramsByEnd[$currentStatut] as $startId => $thresholds) {
            if (!isset($durationsByStart[$startId])) {
                continue;
            }
            $duree = $durationsByStart[$startId];
            $candidate = 'aucun';
            foreach ($thresholds as $threshold) {
                if ($duree >= $threshold['min'] && $duree < $threshold['max']) {
                    $candidate = $threshold['couleur'];
                }
            }
            if ($candidate !== 'aucun' && ($bestDuration === null || $duree > $bestDuration)) {
                $bestStart = (int) $startId;
                $bestDuration = $duree;
                $bestColor = $candidate;
            }
        }

        if ($bestStart !== null) {
            $label = $bestStart . '-' . $currentStatut . ' ' . $bestColor;
            if (!isset($seen[$label])) {
                $seen[$label] = true;
                $labels[] = $label;
            }
        }
    }

    return [
        'colors' => $labels,
        'total_minutes' => $totalMinutes
    ];
}

$error = '';
$demandes = [];

try {
    $pdo = new PDO(
        "mysql:host={$host};dbname={$dbname};charset=utf8mb4",
        $user,
        $pass,
        [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
    );

    $stmt = $pdo->query(
        'SELECT d.id, d.reference, c.nom AS client_nom '
        . 'FROM demande d '
        . 'JOIN client c ON c.id = d.id_client '
        . 'ORDER BY d.id ASC'
    );
    $demandes = $stmt->fetchAll(PDO::FETCH_ASSOC);
} catch (Exception $ex) {
    $error = 'Impossible de charger le suivi. Verifiez que MySQL est demarre et que la base forage existe.';
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Suivi demande PHP </title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; color: #222; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .actions { margin: 15px 0; }
        .error { color: red; margin: 10px 0; }
        .empty { color: #555; }
    </style>
</head>
<body>
<h1>Suivi demande PHP - ETU004173</h1>

<div class="actions">
    <a href="http://localhost:8080/Forage/demande/list">Retour aux demandes</a>
</div>

<?php if ($error !== ''): ?>
    <div class="error"><?= e($error) ?></div>
<?php elseif (empty($demandes)): ?>
    <p class="empty">Aucune demande trouvee.</p>
<?php else: ?>
    <h3>Intervalles par demande</h3>
    <table>
        <thead>
            <tr>
                <th>Reference</th>
                <th>Client</th>
                <th>Intervalles (couleur)</th>
                <th>Duree totale</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($demandes as $demande): ?>
                <?php $suivi = loadSuiviInfo($pdo, (int) $demande['id']); ?>
                <tr>
                    <td><?= e($demande['reference']) ?></td>
                    <td><?= e($demande['client_nom']) ?></td>
                    <td><?= empty($suivi['colors']) ? 'aucun' : e(implode(', ', $suivi['colors'])) ?></td>
                    <td><?= e(formatHours((int) $suivi['total_minutes'])) ?></td>
                </tr>
            <?php endforeach; ?>
        </tbody>
    </table>
<?php endif; ?>
</body>
</html>
