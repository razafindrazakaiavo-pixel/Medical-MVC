-- Mise a jour limitee aux comptes demo dont le mot de passe est encore celui d'origine.
UPDATE utilisateur
SET password_hash = 'DemoMedical123!'
WHERE login IN ('demo.medecin', 'demo.aina', 'demo.mamy', 'demo.voahangy')
  AND password_hash = '6P1IeWnsV1aJbwDLqGh4hQ==:tIs7LHKpZOugbVnPtnsx/Fc1gOYEz/iy02hgsYHliew=';
