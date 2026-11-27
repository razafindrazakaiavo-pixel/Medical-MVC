$ErrorActionPreference = 'Stop'

$jdk = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin'
$javac = Join-Path $jdk 'javac.exe'
$jar = Join-Path $jdk 'jar.exe'
if (!(Test-Path $javac)) {
    $javac = 'javac'
    $jar = 'jar'
}

# Ces deux dossiers ne contiennent que les fichiers générés pour le WAR.
foreach ($directory in @('build/classes', 'build/app')) {
    if (Test-Path -LiteralPath $directory) {
        $resolved = (Resolve-Path -LiteralPath $directory).Path
        $expected = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot $directory))
        if ($resolved -ne $expected) {
            throw "Dossier de compilation inattendu : $resolved"
        }
        Remove-Item -LiteralPath $resolved -Recurse -Force
    }
}
New-Item -ItemType Directory -Force build/classes, build/app/WEB-INF/classes, build/app/WEB-INF/lib | Out-Null

$sources = @(Get-ChildItem src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
& $javac -encoding UTF-8 -cp 'lib/*' -d build/classes $sources
if ($LASTEXITCODE -ne 0) {
    throw 'Compilation echouee'
}

Copy-Item web/* build/app/ -Recurse -Force
Copy-Item build/classes/* build/app/WEB-INF/classes/ -Recurse -Force
Copy-Item lib/Framework.jar build/app/WEB-INF/lib/ -Force
$mysqlDriver = Get-ChildItem lib -Filter 'mysql-connector-j*.jar' | Select-Object -First 1
if ($null -eq $mysqlDriver) { throw 'Pilote MySQL introuvable dans lib.' }
Copy-Item $mysqlDriver.FullName build/app/WEB-INF/lib/ -Force
Copy-Item lib/zxing-core-3.5.3.jar build/app/WEB-INF/lib/ -Force
& $jar cf Medical.war -C build/app .
if ($LASTEXITCODE -ne 0) {
    throw 'Creation du WAR echouee'
}
Write-Host 'Medical.war cree.'


