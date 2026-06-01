<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Statut demande</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-group { margin-top: 12px; }
        .error { color: red; }
        .success { color: green; margin-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .actions { margin: 15px 0; }
    </style>
</head>
<body>
<h1>Statut demande</h1>

<div class="actions">
    <a href="${pageContext.request.contextPath}/demande/list">Retour aux demandes</a>
</div>

<c:if test="${not empty message}">
    <div class="success">${message}</div>
</c:if>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/demande/statut">
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

    <div id="currentStatus" class="form-group" style="display: none;">
        <h3>Statut actuel</h3>
        <table class="info-table">
            <tbody>
                <tr>
                    <th>Statut</th>
                    <td id="currentStatut"></td>
                </tr>
                <tr>
                    <th>Date</th>
                    <td id="currentDate"></td>
                </tr>
                <tr>
                    <th>Duree (min)</th>
                    <td id="currentDuree"></td>
                </tr>
                <tr>
                    <th>Description</th>
                    <td id="currentDescription"></td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="form-group">
        <label for="statusId">Statut :</label>
        <select id="statusId" name="statusId" required>
            <option value="">-- Statut --</option>
            <c:forEach var="status" items="${statuses}">
                <option value="${status.id}">${status.libelle}</option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label for="description">Description :</label>
        <input type="text" id="description" name="description" placeholder="Description">
    </div>

    <div class="form-group">
        <label for="statutDate">Date statut :</label>
        <input type="date" id="statutDate" name="statutDate" required value="${defaultDate}">
    </div>

    <div class="form-group">
        <label for="statutTime">Heure statut :</label>
        <input type="time" id="statutTime" name="statutTime" required value="${defaultTime}">
    </div>

    <div class="form-group">
        <button type="submit">Ajouter statut</button>
    </div>
</form>

<form id="updateForm" method="post" action="${pageContext.request.contextPath}/demande/statut/update" style="display: none;">
    <input type="hidden" id="demandeStatutId" name="demandeStatutId">
    <h3>Modifier statut actuel</h3>
    <div class="form-group">
        <label for="updateStatusId">Statut :</label>
        <select id="updateStatusId" name="statusId" required>
            <option value="">-- Statut --</option>
            <c:forEach var="status" items="${statuses}">
                <option value="${status.id}">${status.libelle}</option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label for="updateDescription">Description :</label>
        <input type="text" id="updateDescription" name="description" placeholder="Description">
    </div>

    <div class="form-group">
        <label for="updateDate">Date statut :</label>
        <input type="date" id="updateDate" name="statutDate" required value="${defaultDate}">
    </div>

    <div class="form-group">
        <label for="updateTime">Heure statut :</label>
        <input type="time" id="updateTime" name="statutTime" required value="${defaultTime}">
    </div>

    <div class="form-group">
        <button type="submit">Modifier statut</button>
    </div>
</form>

<script>
function loadDemande() {
    const reference = document.getElementById('reference').value.trim();
    const info = document.getElementById('demandeInfo');
    const infoError = document.getElementById('demandeInfoError');
    const currentStatus = document.getElementById('currentStatus');
    const updateForm = document.getElementById('updateForm');
    infoError.textContent = '';
    info.style.display = 'none';
    currentStatus.style.display = 'none';
    updateForm.style.display = 'none';
    if (!reference) {
        return;
    }

    fetch('${pageContext.request.contextPath}/demande/info?reference=' + encodeURIComponent(reference))
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
            setStatusInfo(data);
        })
        .catch(() => {
            info.style.display = 'none';
            infoError.textContent = 'Demande introuvable';
            clearStatusInfo();
        });
}

function setInfo(id, value) {
    const el = document.getElementById(id);
    el.textContent = value ? value : '-';
}

function setStatusInfo(data) {
    const currentStatus = document.getElementById('currentStatus');
    const updateForm = document.getElementById('updateForm');
    if (!data.statutId) {
        clearStatusInfo();
        return;
    }
    currentStatus.style.display = 'block';
    updateForm.style.display = 'block';
    setInfo('currentStatut', data.statutLibelle);
    setInfo('currentDate', formatDateTime(data));
    setInfo('currentDuree', data.statutDuree);
    setInfo('currentDescription', data.statutDescription);

    document.getElementById('demandeStatutId').value = data.statutId;
    document.getElementById('updateStatusId').value = data.statutStatusId || '';
    document.getElementById('updateDescription').value = data.statutDescription || '';
    document.getElementById('updateDate').value = data.statutDate || '${defaultDate}';
    document.getElementById('updateTime').value = data.statutTime || '${defaultTime}';
}

function clearStatusInfo() {
    document.getElementById('currentStatus').style.display = 'none';
    document.getElementById('updateForm').style.display = 'none';
    document.getElementById('demandeStatutId').value = '';
}

function formatDateTime(data) {
    if (!data.statutDate && !data.statutTime) {
        return '-';
    }
    if (!data.statutTime) {
        return data.statutDate;
    }
    return data.statutDate + ' ' + data.statutTime;
}
</script>
</body>
</html>
