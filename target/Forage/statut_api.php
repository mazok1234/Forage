<?php
header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: http://localhost:8080');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit;
}

$idDemande = isset($_GET['id_demande']) ? (int) $_GET['id_demande'] : 0;
if ($idDemande <= 0) {
    echo json_encode([]);
    exit;
}

$host = 'localhost';
$dbname = 'forage';
$user = 'root';
$pass = '';

try {
    $pdo = new PDO(
        "mysql:host={$host};dbname={$dbname};charset=utf8mb4",
        $user,
        $pass,
        [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
    );

    $statutStmt = $pdo->prepare(
        'SELECT ds.id_statut, ds.duree_travail, ds.description, ds.date '
        . 'FROM demande_statut ds '
        . 'WHERE ds.id_demande = ? '
        . 'ORDER BY ds.date ASC, ds.id ASC'
    );
    $statutStmt->execute([$idDemande]);
    $statuts = $statutStmt->fetchAll(PDO::FETCH_ASSOC);

    $paramsStmt = $pdo->query('SELECT idStatut1, idStatut2, duree_travail, alerte_couleur FROM parametres');
    $params = [];
    foreach ($paramsStmt->fetchAll(PDO::FETCH_ASSOC) as $row) {
        $id1 = (int) $row['idStatut1'];
        $id2 = (int) $row['idStatut2'];
        if (!isset($params[$id1])) {
            $params[$id1] = [];
        }
        if (!isset($params[$id1][$id2])) {
            $params[$id1][$id2] = [];
        }
        $params[$id1][$id2][] = [
            'duree' => (int) $row['duree_travail'],
            'couleur' => trim((string) $row['alerte_couleur'])
        ];
    }
    foreach ($params as $id1 => $pairs) {
        foreach ($pairs as $id2 => $values) {
            usort($values, function ($a, $b) {
                return $a['duree'] <=> $b['duree'];
            });
            $params[$id1][$id2] = $values;
        }
    }

    $paramsByEnd = [];
    foreach ($params as $id1 => $pairs) {
        foreach ($pairs as $id2 => $values) {
            if (!isset($paramsByEnd[$id2])) {
                $paramsByEnd[$id2] = [];
            }
            $paramsByEnd[$id2][$id1] = $values;
        }
    }

    $result = [];
    $count = count($statuts);
    for ($i = 0; $i < $count; $i++) {
        $row = $statuts[$i];
        $currentStatut = (int) $row['id_statut'];
        $color = 'aucun';
        $intervalle = '';
        $bestStart = null;
        if (isset($paramsByEnd[$currentStatut]) && $i > 0) {
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

            $bestDuration = null;
            $bestColor = 'aucun';
            foreach ($paramsByEnd[$currentStatut] as $startId => $thresholds) {
                if (!isset($durationsByStart[$startId])) {
                    continue;
                }
                $duree = $durationsByStart[$startId];
                $candidate = 'aucun';
                foreach ($thresholds as $threshold) {
                    if ($duree > $threshold['duree']) {
                        $candidate = $threshold['couleur'];
                    }
                }
                if ($candidate !== 'aucun') {
                    if ($bestDuration === null || $duree > $bestDuration) {
                        $bestDuration = $duree;
                        $bestColor = $candidate;
                        $bestStart = (int) $startId;
                    }
                }
            }
            if ($bestDuration !== null) {
                $color = $bestColor;
                $intervalle = $bestStart . '-' . $currentStatut;
            }
        }

        $result[] = [
            'id_statut' => $currentStatut,
            'duree_travail' => $row['duree_travail'] === null ? null : (int) $row['duree_travail'],
            'description' => $row['description'] ?? '',
            'date' => $row['date'],
            'couleur' => $color,
            'intervalle' => $intervalle
        ];

    }

    echo json_encode($result);
} catch (Exception $ex) {
    http_response_code(500);
    echo json_encode(['error' => 'db_error']);
}
