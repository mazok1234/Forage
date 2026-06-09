<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Suivi demande</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-group { margin-top: 12px; }
        .error { color: red; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .actions { margin: 15px 0; }
    </style>
</head>
<body>
<h1>Suivi demande</h1>

<div class="actions">
    <a href="${pageContext.request.contextPath}/demande/list">Retour aux demandes</a>
</div>

<div id="statusHistory" class="form-group">
    <h3>Intervalles par demande</h3>
    <table class="info-table" id="demandeColors">
        <thead>
            <tr>
                <th>Reference</th>
                <th>Client</th>
                <th>Intervalles (couleur)</th>
                <th>Duree totale</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="demande" items="${demandes}">
                <tr data-id="${demande.id}">
                    <td>${demande.reference}</td>
                    <td>${demande.client.nom}</td>
                    <td class="colors">Chargement...</td>
                    <td class="total-hours">Chargement...</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script>
function loadAllColors() {
    const rows = document.querySelectorAll('#demandeColors tbody tr');
    rows.forEach(row => {
        const demandeId = row.dataset.id;
        const cell = row.querySelector('.colors');
        const totalCell = row.querySelector('.total-hours');
        if (!demandeId) {
            cell.textContent = 'aucun';
            totalCell.textContent = '0';
            return;
        }
        fetch('http://localhost:8081/statut_api.php?id_demande=' + encodeURIComponent(demandeId))
            .then(response => response.json())
            .then(items => {
                if (!Array.isArray(items) || items.length === 0) {
                    cell.textContent = 'aucun';
                    totalCell.textContent = '0';
                    return;
                }
                const labels = [];
                const seen = new Set();
                let totalHours = 0;
                items.forEach(item => {
                    if (parseInt(item.id_statut, 10) === 8) {
                        totalHours = parseFloat(item.duree_total_heures || 0);
                    }
                    const color = (item.couleur || 'aucun').toLowerCase();
                    const intervalle = item.intervalle || '';
                    if (!intervalle || color === 'aucun') {
                        return;
                    }
                    const key = intervalle + ':' + color;
                    if (!seen.has(key)) {
                        seen.add(key);
                        labels.push({ intervalle, color });
                    }
                });
                if (labels.length === 0) {
                    cell.textContent = 'aucun';
                } else {
                    cell.textContent = labels.map(item => {
                        return item.intervalle + ' ' + item.color;
                    }).join(', ');
                }
                totalCell.textContent = totalHours > 0 ? totalHours.toFixed(2) + ' h' : '0';
            })
            .catch(() => {
                cell.textContent = 'aucun';
                totalCell.textContent = '0';
            });
    });
}

document.addEventListener('DOMContentLoaded', loadAllColors);
</script>
</body>
</html>
