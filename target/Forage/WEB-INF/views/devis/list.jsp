<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Liste des devis</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
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
<h1>Liste des devis</h1>

<div class="actions">
    <a href="${pageContext.request.contextPath}/devis/demandes">Demandes approuvees</a>
    <a href="${pageContext.request.contextPath}/demande/list">Retour aux demandes</a>
</div>

<c:if test="${not empty message}">
    <div class="success">${message}</div>
</c:if>

<c:choose>
    <c:when test="${empty devisList}">
        <p>Aucun devis trouve.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Demande</th>
                    <th>Client</th>
                    <th>Lieu</th>
                    <th>Libelle</th>
                    <th>Montant</th>
                    <th>Date</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="devis" items="${devisList}">
                    <c:set var="devisStatutInfo" value="${latestDevisStatuts[devis.id]}" />
                    <tr>
                        <td>${devis.id}</td>
                        <td>${devis.demande.id}</td>
                        <td>${devis.demande.client.nom}</td>
                        <td>${devis.demande.lieu}</td>
                        <td>${devis.libelle}</td>
                        <td>${devis.montant}</td>
                        <td>${devis.date}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty devisStatutInfo}">${devisStatutInfo.statut.libelle}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="action-links">
                            <c:choose>
                                <c:when test="${not empty devisStatutInfo && devisStatutInfo.statut.libelle == 'En attente'}">
                                    <form method="post" action="${pageContext.request.contextPath}/devis/accept/${devis.id}">
                                        <button type="submit">Accepter</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/devis/refuse/${devis.id}">
                                        <button type="submit">Refuser</button>
                                    </form>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
