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
