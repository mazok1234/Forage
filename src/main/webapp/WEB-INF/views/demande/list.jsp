<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Liste des Demandes</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .success { color: green; margin-bottom: 10px; }
        .actions { margin-top: 15px; }
        .action-links a, .action-links button { margin-right: 6px; }
        .action-links form { display: inline; }
        .status-form select, .status-form input { margin-right: 6px; }
    </style>
</head>
<body>
<h1>Liste des Demandes</h1>

<c:if test="${not empty message}">
    <div class="success">${message}</div>
</c:if>

<c:choose>
    <c:when test="${empty demandes}">
        <p>Aucune demande trouvée.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>Reference</th>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Commune</th>
                    <th>Lieu</th>
                    <th>Statut</th>
                    <th>Date statut</th>
                    <th>Duree (min)</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="demande" items="${demandes}">
                    <tr>
                        <c:set var="statutInfo" value="${latestStatuts[demande.id]}" />
                        <td>${demande.reference}</td>
                        <td>${demande.id}</td>
                        <td>${demande.client.nom}</td>
                        <td>${demande.commune.libelle}</td>
                        <td>${demande.lieu}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty statutInfo}">${statutInfo.statut.libelle}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty statutInfo}">${statutInfo.date}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty statutInfo}">${statutInfo.dureeTravail}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty statutInfo}">${statutInfo.description}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="action-links">
                            <a href="${pageContext.request.contextPath}/demande/edit/${demande.id}">Modifier</a>
                            <form method="post" action="${pageContext.request.contextPath}/demande/delete/${demande.id}">
                                <button type="submit" onclick="return confirm('Supprimer cette demande ?');">Supprimer</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<div class="actions">
    <a href="${pageContext.request.contextPath}/demande/">Creer une nouvelle demande</a>
    <a href="${pageContext.request.contextPath}/demande/statut">Statut</a>
    <!-- <a href="${pageContext.request.contextPath}/demande/suivi">Suivi</a> -->
    <a href="http://localhost:8081/suivi.php">Suivi</a>
    <a href="${pageContext.request.contextPath}/devis/create">Devis</a>
</div>
</body>
</html>
