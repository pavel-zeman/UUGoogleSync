<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="cs" xml:lang="cs">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>UUGoggleSync - Aktualizace schůzek v kalendáři Google na základě dat v Unicorn Universe</title> 
  <style type="text/css">
    H1 { font-family: Arial,sans-serif; font-size: 15pt; }
    H2 { font-family: Arial,sans-serif; font-size: 12pt; }
    P, LI  { font-family: Arial,sans-serif; font-size: 11pt; }
  </style>
</head>
<body>

<h1>Základní popis</h1>
<p>
Aplikace UUGoogleSync automaticky přenáší schůzky plánované v Unicorn Universe do kalendáře Google (opačný směr není v současné době podporovaný).
Pokud máte nadefinovaných více kalendářů, přenášejí se schůzky výhradně do primárního kalendáře (pokud by byl zájem o synchronizaci s jiným kalendářem, tak to lze snadno doimplementovat).
Z kalendáře Google je možné schůzky dále synchronizovat s <a href="http://support.google.com/calendar/bin/answer.py?hl=en&amp;answer=98563">Microsoft Outlookem</a>,
<a href="http://support.google.com/mobile/bin/answer.py?hl=en&amp;answer=138740">chytrotelefony s iOS</a> nebo Androidem a dalšími zařízeními a aplikacemi.
</p>

<p>
Pro načítání schůzek z Unicorn Universe se používá přímo uživatelské rozhraní Unicorn Universe. 
Aplikace se tedy přihlásí do Unicorn Universe s použitím vašich přístupových kódů, prokliká několik týdnů vašeho kalendáře (na základě konfigurace) 
a parsováním získaného HTML načte všechny schůzky.
Důsledkem použití tohoto postupu je to, že při změně uživatelského rozhraní Unicorn Universe přestane aplikace fungovat. Bohužel o lepším způsobu (API?) načítání
schůzek z Unicorn Universe nevím.
</p>

<p>
Pro komunikaci s kalendářem Google se používá <a href="http://code.google.com/intl/cs/apis/calendar/">API poskytované přímo Googlem</a>. Výhodou je jednoduchost 
implementace a nezávislost na GUI, na druhou stranu je přes API možno zdarma odeslat maximálně 10 tisíc požadavků denně.
</p>

<p>
Aplikace je k dispozici zdarma jako plně funkční alfa verze, nicméně v současné době chybí zejména korektní ošetření chybových stavů. Použití aplikace je na vlastní riziko.
</p>

<h1>Instalace</h1>
<ol>
  <li>
    Pro běh aplikace je vyžadováno JRE verze 6 nebo vyšší na MS Windows (pro ostatní platformy viz <a href="#FAQ_LINUX">FAQ</a>).
    Pokud toto JRE nemáte k dispozici, nainstalujte si ho.
  </li>
  <li>Stáhněte si <a href="UUGoogleSync.zip">aplikační archiv</a> a rozbalte ho do libovolného adresáře.</li>
  <li>Spusťte aplikaci <tt>UUGoogleSyncConfigurator.exe</tt> a nakonfigurujte alespoň následující údaje
  (všechny údaje si aplikace pamatuje v konfiguračním souboru <tt>config/UUGoogleSync.properties</tt>, některé údaje se šifrují):
    <ul>
      <li>Access code 1 a access code 2 pro přihlášení do Unicorn Universe</li>
      <li>Přístup do vašeho kalendáře Google pomocí speciálního URL (detaily jsou uvedené přímo v aplikaci)</li>
    </ul>
  </li>
  <li>
    Pošlete mi vaši gmailovou adresu, kterou používáte pro přihlášení ke kalendáři, abych ji mohl povolit. 
    Bez povolení adresy nebude synchronizace schůzek fungovat (viz také <a href="#FAQ_REGISTRATION">FAQ</a>).
    Pokud nevíte, kdo jsem já, tak pravděpodobně tyto stránky nejsou určené pro vás. 
  </li>
  <li>Spusťte vlastní synchronizační proces, tj. aplikaci <tt>UUGoogleSync.exe</tt>.
  Aplikace běží na pozadí s tím, že do systraye přidává vlastní ikonu s tooltipem informujícím o aktuálním stavu. Pokud chcete aplikaci automaticky startovat
  při přihlášení uživatele, použijte standardní mechanismy operačního systému.
  </li>   
</ol>

<h1>Historie verzí</h1>
<h2>2012-01-04</h2>
<ul>
<li>ISS2 - Aplikaci je nyní možné instalovat i do adresáře s mezerou v názvu (takže si s chutí užijte Program Files).</li>
<li>ISS3 - Správnost přihlašovacích údajů do Unicorn Universe je kontrolována již při konfiguraci.
Navíc pokud se zjistí neplatnost přihlašovacích údajů během synchronizace, vypíše se do logu rozumné chybové hlášení.</li>
<li>ISS4 - Entita &amp;amp; je při načítání dat z Unicorn Universe korektně přeložena na &amp;.</li>
</ul>

<h2>2012-01-01</h2>
<ul>
<li>Doplněno zipování komunikace s Unicorn Universe, čímž se množství stažených dat snížilo na cca 1/8.</li>
<li>Pokud se nepovede synchronizace, provede se další pokus již za 10 minut (v konfiguračním souboru lze případně ještě snížit). 
Důvodem je rychlejší synchronizace po obnovení provozu počítače.</li>
<li>Doplněno odhlášení z Unicorn Universe po načtení všech dat.</li>
</ul>

<h2>2011-12-23</h2>
<p>První verze</p>

<h1>FAQ</h1>
<h2><a name="FAQ_REGISTRATION">Proč je nutné před použitím aplikace "registrovat" vlastní gmailovou adresu?</a></h2>
<p>Důvody jsou následující:</p>
<ol>
  <li>
    Počet volání Google API pro synchronizaci kalendáře je omezen na 10 tisíc denně (jde o celkové číslo pro všechny uživatele aplikace dohromady). 
    Při překročení uvedeného počtu volání přestane aplikace fungovat. Jedinou možností, jak omezit celkový počet volání, je omezit počet uživatelů, kteří aplikaci používají.
  </li>
  <li>
    Jde o alfa verzi, nechci tedy, aby se aplikace příliš šířila.
  </li>
</ol>

<h2 >Jak je to s bezpečností celého řešení?</h2>
<p>Bezpečnost má v tomto případě několik aspektů:</p>
<ol>
  <li>
    Bezpečnost síťové komunikace - Všechna data jsou přenášena přes šifrované spojení (https), nemohou být tedy zachycena ani modifikována neoprávněnou osobou. 
    Jedinou výjimkou je ověření, zda vaše gmailová adresa může aplikaci používat. Pro tyto účely se šifrované spojení nepoužívá a vaše gmailová adresa je tak přenášena v čitelné podobě.
  </li>
  <li>
    Bezpečnost konfiguračních dat - Konfigurační data (soubor <tt>config/UUGoogleSync.properties</tt>) obsahují citlivé údaje. Konkrétně jde o vaše přístupové kódy do Unicorn Universe a 
    tokeny pro přístup ke kalendáři Google (tyto tokeny lze použít výhradně k programovému přístupu ke kalendáři, nelze je zneužít pro přístup k žádné jiné službě Google).
    Všechna tato citlivá data jsou šifrována, nicméně šikovný Java programátor dokáže snadno zjistit šifrovací algoritmus a šifrovací klíč a data dešifrovat. Doporučuji tedy k souboru 
    <tt>config/UUGoogleSync.properties</tt> nastavit přístupová práva pouze pro vašeho uživatele.     
  </li>
  <li>
    Riziko úniku citlivých informací mimo Unicorn Universe - Samotné schůzky v Unicorn Universe mohou obsahovat citlivá data.
    Replikací těchto dat do kalendáře Google ztrácíte nad těmito daty kontrolu, data se tedy teoreticky mohou dostat do nepovolaných rukou. 
    Tento problém aplikace nijak neřeší (a v principu ani nijak řešit nemůže).
  </li>   
</ol>

<h2>Nastavil jsem si synchronizaci každou půlhodinu, ale data se synchronizují pouze jednou za hodinu. Proč?</h2>
<p>
Minimální možný interval pro synchronizaci je jedna hodina. Jakýkoliv kratší interval se ignoruje.
Důvodem je výše uvedené omezení na počet volání Google API a minimalizace zátěže na Unicorn Universe.
</p>

<h2>Je nutné spouštět nějakou aplikaci na mém vlastním počítači? Nemohl by synchronizaci provádět nějaký server?</h2>
<p>Mohl. Dokonce je to technicky velice jednoduché. Problém je ovšem v tom, že by na jednom místě byly přístupové kódy do Unicorn Universe pro všechny uživatele aplikace,
což považuji za relativně velké bezpečnostní riziko. Proto o této variantě zatím neuvažuji.</p>

<h2><a name="FAQ_LINUX">Běží aplikace i pod Linuxem, Mac OS X nebo jiným mým oblíbeným operačním systémem?</a></h2>
<p>
Aplikace je implementovaná v Javě SE a měla by tedy fungovat pod libovolným operačním systémem, pro který je k dispozici JRE verze 6 nebo vyšší.
Pro spuštění aplikace pod jiným operačním systémem než MS Windows nelze samozřejmě použít dodávané EXE soubory, ale je nutné použít přímo JRE a aplikační JARy.
Pro konkrétní detaily mě kontaktujte.
</p>

<h2>Proč má taková kravina více než 3 MB?</h2>
<p>Samotná aplikace má méně než 50 KB. Zbytek jsou zejména knihovny Google API. Zkuste se tedy zeptat u Googlu.</p>

<h2>Aplikace nejde spustit, vypisuje chybu, nesynchronizuje schůzky nebo se chová jinak špatně. Co s tím?</h2>
<p>
Zkontrolujte, zda máte nainstalované JRE verze alespoň 6, případně toto JRE nainstalujte a zkuste to znovu.
Pokud ani potom aplikace nefunguje, zkuste zkontrolovat aplikační log (<tt>log/UUGoogleSync.log</tt>) nebo mi tento log pošlete. Určitě to nějak vyřešíme.
</p>

<h2>Přišel jsem o schůzky, které jsem měl v kalendáři Google před spuštěním synchronizace</h2>
<p>Aplikace je v alfa verzi a používáte ji na vlastní riziko, takže sorry (ale já ji používám už cca týden a zatím je to bez problémů).</p>

<h2>Proč je ta ikona v systray tak hnusná?</h2>
<p>
Bohužel jde o <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6453521">problém Javy</a> (Sun/Oracle na něj kašle již více než 5 let!), 
která pro ikony v systray nepodporuje korektně průhlednost. Ikona je potom ošklivě zubatá.
</p>
<p>Pokud pozadí ikony vyplním konkrétní barvou a udělám ho neprůhledné, tak ikona vypadá výrazně lépe. 
Bohužel toto řešení není univerzální, protože uživateli s modrým systray se mé černé pozadí příliš líbit nebude.
</p>


<h2>Proč mají schůzky v kalendáři Google postfix (UU)?</h2>
<p>Smyslem tohoto postfixu je vizuálně rozlišit schůzky zadané přímo do kalendáře Google (bez postfixu) a 
schůzky načtené z Unicorn Universe (s postfixem). Postfix se nijak nepoužívá pro účely synchronizace. 
</p>

</body>
</html>