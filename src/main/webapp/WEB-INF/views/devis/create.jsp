<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Creation devis</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-group { margin-top: 12px; }
        .error { color: red; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .success { color: green; margin-bottom: 10px; }
        .actions { margin: 15px 0; }
        .action-links a, .action-links button { margin-right: 6px; }
        .action-links form { display: inline; }
    </style>
</head>
<body>
<h1>Devis</h1>

<div class="actions">
    <a href="${pageContext.request.contextPath}/demande/list">Retour aux demandes</a>
</div>

<c:if test="${not empty message}">
    <div class="success">${message}</div>
</c:if>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/devis/create">
    <div class="form-group">
        <label for="reference">Reference demande :</label>
        <select id="reference" name="reference" required onchange="loadDemande()">
            <option value="">-- Reference --</option>
            <c:forEach var="demande" items="${demandes}">
                <option value="${demande.reference}">${demande.reference} - ${demande.client.nom}</option>
            </c:forEach>
        </select>
    </div>
    <div id="demandeInfoError" class="error"></div>
    <div id="demandeInfo" class="form-group" style="display: none;">
        <h3>Infos demande</h3>
        <table class="info-table">
            <tbody>
                <tr>
                    <th>Reference</th>
                    <td id="infoReference"></td>
                </tr>
                <tr>
                    <th>Client</th>
                    <td id="infoClient"></td>
                </tr>
                <tr>
                    <th>Contact</th>
                    <td id="infoContact"></td>
                </tr>
                <tr>
                    <th>Adresse</th>
                    <td id="infoAdresse"></td>
                </tr>
                <tr>
                    <th>Region</th>
                    <td id="infoRegion"></td>
                </tr>
                <tr>
                    <th>District</th>
                    <td id="infoDistrict"></td>
                </tr>
                <tr>
                    <th>Commune</th>
                    <td id="infoCommune"></td>
                </tr>
                <tr>
                    <th>Lieu</th>
                    <td id="infoLieu"></td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="form-group">
        <label for="typeDevis">Type devis :</label>
        <select id="typeDevis" name="typeDevisId" required>
            <option value="">-- Type --</option>
            <c:forEach var="type" items="${typesDevis}">
                <option value="${type.id}">${type.libelle}</option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label for="statutDate">Date :</label>
        <input type="date" id="statutDate" name="statutDate" required value="${defaultDate}">
    </div>
    <div class="form-group">
        <label for="statutTime">Heure :</label>
        <input type="time" id="statutTime" name="statutTime" required value="${defaultTime}">
    </div>

    <table id="detailsTable">
        <thead>
            <tr>
                <th>Libelle</th>
                <th>Qte</th>
                <th>PU</th>
                <th>Montant</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><input type="text" name="detailLibelle" required></td>
                <td><input type="number" name="detailQuantite" min="1" required onblur="updateTotal()" oninput="updateTotal()"></td>
                <td><input type="number" name="detailPrixUnitaire" min="0" step="0.01" required onblur="updateTotal()" oninput="updateTotal()"></td>
                <td class="line-total">0.00</td>
                <td><button type="button" onclick="removeDetail(this)">-</button></td>
            </tr>
        </tbody>
    </table>

    <div class="form-group">
        <button type="button" onclick="addDetail()">+</button>
        <strong>Total :</strong> <span id="totalDevis">0.00</span>
    </div>

    <div class="form-group">
        <button type="submit">Enregistrer devis</button>
    </div>
</form>

<script>
function loadDemande() {
    const reference = document.getElementById('reference').value.trim();
    const info = document.getElementById('demandeInfo');
    const infoError = document.getElementById('demandeInfoError');
    infoError.textContent = '';
    info.style.display = 'none';
    if (!reference) {
        return;
    }

    fetch('${pageContext.request.contextPath}/devis/demande-info?reference=' + encodeURIComponent(reference))
        .then(response => {
            if (!response.ok) {
                throw new Error('Demande introuvable');
            }
            return response.json();
        })
        .then(data => {
            infoError.textContent = '';
            info.style.display = 'block';
            setInfo('infoReference', data.reference);
            setInfo('infoClient', data.clientNom);
            setInfo('infoContact', data.clientContact);
            setInfo('infoAdresse', data.clientAdresse);
            setInfo('infoRegion', data.region);
            setInfo('infoDistrict', data.district);
            setInfo('infoCommune', data.commune);
            setInfo('infoLieu', data.lieu);
        })
        .catch(() => {
            info.style.display = 'none';
            infoError.textContent = 'Demande introuvable';
        });
}

function setInfo(id, value) {
    const el = document.getElementById(id);
    el.textContent = value ? value : '-';
}

function addDetail() {
    const tbody = document.getElementById('detailsTable').querySelector('tbody');
    const row = document.createElement('tr');
    row.innerHTML =
        '<td><input type="text" name="detailLibelle" required></td>' +
        '<td><input type="number" name="detailQuantite" min="1" required onblur="updateTotal()" oninput="updateTotal()"></td>' +
        '<td><input type="number" name="detailPrixUnitaire" min="0" step="0.01" required onblur="updateTotal()" oninput="updateTotal()"></td>' +
        '<td class="line-total">0.00</td>' +
        '<td><button type="button" onclick="removeDetail(this)">-</button></td>';
    tbody.appendChild(row);
}

function removeDetail(button) {
    const tbody = document.getElementById('detailsTable').querySelector('tbody');
    if (tbody.rows.length <= 1) {
        return;
    }
    button.closest('tr').remove();
    updateTotal();
}

function updateTotal() {
    const tbody = document.getElementById('detailsTable').querySelector('tbody');
    let total = 0;
    Array.from(tbody.rows).forEach(row => {
        const qte = parseFloat(row.querySelector('input[name="detailQuantite"]').value) || 0;
        const pu = parseFloat(row.querySelector('input[name="detailPrixUnitaire"]').value) || 0;
        const lineTotal = qte * pu;
        row.querySelector('.line-total').textContent = lineTotal.toFixed(2);
        total += lineTotal;
    });
    document.getElementById('totalDevis').textContent = total.toFixed(2);
}
</script>
</body>
</html>
