# Medical — gestion des patients

Application MVC construite avec `Framework.jar`, Java 17, Tomcat 10.1 et MariaDB. Le connecteur JDBC MySQL fourni dans `lib` fonctionne avec MariaDB 10.4 testé sur cet ordinateur.

## Organisation

```text
src/iavo/main/
  controller/       Routes et contrôleurs du framework
  model/            Modèles Patient et User
  service/          Validation et logique métier
  dao/              Requêtes SQL préparées
  config/           Connexion à MariaDB
  Store.java        Lecture des anciennes données pour la migration
web/WEB-INF/jsp/    Vues JSP (commun, authentification, patient)
web/assets/css/     Feuille de style
sql/schema.sql      Création de la base et des tables
lib/                Framework, API Servlet et pilote JDBC
```

## Base de données

Démarrer MariaDB sur `localhost:3306`. Le schéma se crée une seule fois :

```powershell
Get-Content -Raw -Encoding UTF8 .\sql\schema.sql | & 'C:\xampp\mysql\bin\mysql.exe' -h 127.0.0.1 -P 3306 -u root
```

Si le compte MariaDB a un mot de passe, ajouter `-p` à la commande ; il sera demandé. Ce script ne supprime pas les tables existantes. La configuration par défaut est : base `medical`, utilisateur `root`, mot de passe vide. Pour un autre compte, définir les variables d'environnement **dans le même terminal avant de démarrer Tomcat et d'exécuter les commandes Java** :

```powershell
$env:MEDICAL_DB_URL = 'jdbc:mysql://localhost:3306/medical?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Africa/Nairobi'
$env:MEDICAL_DB_USER = 'votre_utilisateur'
$env:MEDICAL_DB_PASSWORD = Read-Host 'Mot de passe MariaDB'
```

Utiliser un compte MariaDB avec les droits nécessaires sur la seule base `medical` en dehors des tests locaux. Ne pas enregistrer de mot de passe dans le projet.

## Compiler et démarrer

```powershell
.\deploy.bat
.\start-tomcat.bat
```

Ouvrir http://localhost:8081/Medical/index.html. Utiliser `./deploy.bat --build-only` pour compiler sans déployer, et `./stop-tomcat.bat` pour arrêter Tomcat. Les chemins Java et Tomcat sont les mêmes que dans Test-sprint ; ajuster les variables si nécessaire.

## Comptes et migration

Après la compilation et la création des tables, créer un médecin dans un terminal interactif :

```powershell
.\creer-medecin.bat nom_medecin
```

Le mot de passe du médecin est demandé sans affichage et doit contenir au moins 10 caractères. Le médecin crée ensuite les patients et leurs comptes dans l'application. Les nouveaux mots de passe sont enregistrés en clair dans la colonne `password_hash`. Les anciens mots de passe hachés restent reconnus pour préserver les comptes existants. Cette configuration est réservée aux essais locaux.

Si l'ancienne application a enregistré des données dans `%USERPROFILE%\medical-data\medical.bin`, les importer **avant d'ajouter d'autres données dans MariaDB**, avec le même utilisateur Windows et les mêmes variables de connexion :

```powershell
.\importer-anciennes-donnees.bat
```

L'import conserve les identifiants et mots de passe existants, ne modifie pas le fichier source et annule toute l'opération en cas de doublon ou d'erreur. Pour un ancien fichier dans un autre répertoire, lancer :

```powershell
& 'C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin\java.exe' '-Dmedical.data=CHEMIN_DU_DOSSIER' -cp 'build/classes;lib/*' iavo.main.service.LegacyImport
```

Le public peut accéder à l'accueil, le médecin gère les patients et le patient voit son propre profil. Pour des données médicales réelles, prévoir HTTPS, sauvegardes, droits limités et sécurité renforcée.

## Données de démonstration

Le script `sql/donnees-demo.sql` ajoute trois patients fictifs et quatre comptes sans remplacer les entrées déjà présentes. Il peut être exécuté à nouveau :

```powershell
Get-Content -Raw -Encoding UTF8 .\sql\donnees-demo.sql | & 'C:\xampp\mysql\bin\mysql.exe' -h 127.0.0.1 -P 3306 -u root --default-character-set=utf8mb4
```

Identifiant médecin : `demo.medecin`. Identifiants patients : `demo.aina`, `demo.mamy`, `demo.voahangy`. Le mot de passe commun est `DemoMedical123!`. Ces comptes connus et ces données fictives servent uniquement aux essais locaux.


## QR codes des patients

Chaque QR code contient une URL vers `/api/patient?id=<identifiant>`. Cette API renvoie uniquement l'identifiant, le nom et le prénom du patient au format JSON. Le médecin ou le patient concerné doit être connecté dans le navigateur qui ouvre l'URL ; les autres utilisateurs n'ont pas accès aux informations. Le médecin peut le voir depuis la liste ou le dossier du patient ; le patient connecté peut voir le sien sur son profil. L'accès aux images est réservé au médecin et au patient concerné.

Les fichiers PNG sont créés lorsqu'ils sont demandés, dans `%USERPROFILE%\medical-data\qrcodes`, hors du site web. Pour générer à l'avance les QR codes manquants de tous les patients, après compilation et démarrage de MariaDB :

```powershell
.\generer-qrcodes.bat
```

Les identifiants de connexion MariaDB sont les mêmes que pour l'application. Pour choisir un autre répertoire, ajouter `-Dmedical.qr.dir=CHEMIN` à la commande Java et à la configuration de Tomcat. Une image conservée après la suppression d'un patient n'est plus accessible par l'application.

L'adresse utilisée pour les QR codes est `http://172.16.1.156:8081/Medical` (adresse Wi-Fi actuelle de cet ordinateur). Le téléphone doit pouvoir joindre cet ordinateur sur le réseau. Si l'adresse change ou pour utiliser un domaine, définir `MEDICAL_PUBLIC_BASE_URL` avant de démarrer Tomcat **et** avant d'exécuter le programme autonome, puis régénérer les QR codes :

```powershell
$env:MEDICAL_PUBLIC_BASE_URL = 'http://ADRESSE_DU_PC:8081/Medical'
.\generer-qrcodes.bat
.\start-tomcat.bat
```

Redémarrer Tomcat si nécessaire. Pour une utilisation réelle, utiliser HTTPS et limiter l'accès au réseau ; les anciens QR codes imprimés continuent de contenir leur ancienne URL. Les fichiers sont versionnés selon l'URL afin qu'un changement d'adresse crée une nouvelle image sans modifier les anciennes.

