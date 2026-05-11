<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Demandes approuvees</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .actions { margin: 15px 0; }
        .action-links a { margin-right: 8px; }
    </style>
</head>
<body>
<h1>Demandes approuvees</h1>

<div class="actions action-links">
    <a href="${pageContext.request.contextPath}/devis/list">Voir les devis</a>
    <a href="${pageContext.request.contextPath}/demande/list">Retour aux demandes</a>
</div>

<c:choose>
    <c:when test="${empty demandes}">
        <p>Aucune demande approuvee.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Commune</th>
                    <th>Lieu</th>
                    <th>Statut</th>
                    <th>Date statut</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="demande" items="${demandes}">
                    <tr>
                        <c:set var="statutInfo" value="${latestStatuts[demande.id]}" />
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
                                <c:when test="${not empty statutInfo}">${statutInfo.description}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="action-links">
                            <a href="${pageContext.request.contextPath}/devis/create/${demande.id}">Creer devis</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
