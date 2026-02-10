-- PostgreSQL: tronquer toutes les tables listées, réinitialiser les séquences et appliquer CASCADE pour les FK
TRUNCATE TABLE image,
    avancement,
    signalement_statut,
    tentative,
    signalement,
    users,
    entreprise,
    statut_signalement,
    profil
RESTART IDENTITY CASCADE;
