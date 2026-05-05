<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Nouvelle Demande</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        select, input { width: 100%; padding: 8px; box-sizing: border-box; }
        button { padding: 10px 20px; background-color: #007bff; color: white; border: none; cursor: pointer; }
        button:hover { background-color: #0056b3; }
        .error { color: red; }
    </style>
</head>
<body>
<h1>Créer une Nouvelle Demande</h1>
<c:if test="${not empty error}"><p class="error">${error}</p></c:if>

<form method="post" action="${pageContext.request.contextPath}/demande/create">
    <div class="form-group">
        <label for="client">Client :</label>
        <select name="clientId" id="client" required>
            <option value="">-- Sélectionner un client --</option>
            <c:forEach var="client" items="${clients}">
                <option value="${client.id}">${client.nom}</option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label for="region">Région :</label>
        <select name="regionId" id="region" required onchange="loadDistricts()">
            <option value="">-- Sélectionner une région --</option>
            <c:forEach var="region" items="${regions}">
                <option value="${region.id}">${region.libelle}</option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label for="district">District :</label>
        <select name="districtId" id="district" required onchange="loadCommunes()">
            <option value="">-- Sélectionner un district --</option>
        </select>
    </div>

    <div class="form-group">
        <label for="commune">Commune :</label>
        <select name="communeId" id="commune" required>
            <option value="">-- Sélectionner une commune --</option>
        </select>
    </div>
    <div class="lieu">
        <label for="lieu">Lieu :</label>
        <input type="text" name="lieu" id="lieu" required>
    </div>

    <button type="submit">Créer la Demande</button>
</form>

<script>
function loadDistricts() {
    const regionId = document.getElementById('region').value;
    const districtSelect = document.getElementById('district');
    const communeSelect = document.getElementById('commune');
    
    districtSelect.innerHTML = '<option value="">-- Sélectionner un district --</option>';
    communeSelect.innerHTML = '<option value="">-- Sélectionner une commune --</option>';
    
    if (regionId) {
        fetch('${pageContext.request.contextPath}/demande/districts/' + regionId)
            .then(response => response.json())
            .then(data => {
                data.forEach(district => {
                    const option = document.createElement('option');
                    option.value = district.id;
                    option.textContent = district.libelle;
                    districtSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Erreur:', error));
    }
}

function loadCommunes() {
    const districtId = document.getElementById('district').value;
    const communeSelect = document.getElementById('commune');
    
    communeSelect.innerHTML = '<option value="">-- Sélectionner une commune --</option>';
    
    if (districtId) {
        fetch('${pageContext.request.contextPath}/demande/communes/' + districtId)
            .then(response => response.json())
            .then(data => {
                data.forEach(commune => {
                    const option = document.createElement('option');
                    option.value = commune.id;
                    option.textContent = commune.libelle;
                    communeSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Erreur:', error));
    }
}
</script>
</body>
</html>
