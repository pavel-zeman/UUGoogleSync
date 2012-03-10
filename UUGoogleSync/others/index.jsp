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
Pokud máte nadefinovaných více kalendářů, je možné určit, do kterého kalendáře se schůzky přenáší (defaultně se použije primární kalendář).
Z kalendáře Google je možné schůzky dále synchronizovat s <a href="http://support.google.com/calendar/bin/answer.py?hl=en&amp;answer=98563">Microsoft Outlookem</a>,
<a href="http://support.google.com/mobile/bin/answer.py?hl=en&amp;answer=138740">chytrotelefony s iOS</a>,
<a href="http://support.google.com/mobile/bin/answer.py?hl=en&answer=147951">Symbianem</a> nebo Androidem a dalšími zařízeními a aplikacemi.
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
  <li>Stáhněte si <a href="UUGoogleSync.zip?2012-03-10">aplikační archiv</a> a rozbalte ho do libovolného adresáře.</li>
  <li>Spusťte aplikaci <tt>UUGoogleSyncConfigurator.exe</tt> a nakonfigurujte alespoň následující údaje
  (všechny údaje si aplikace pamatuje v konfiguračním souboru <tt>config/UUGoogleSync.properties</tt>, některé údaje se šifrují):
    <ul>
      <li>Access code 1 a access code 2 pro přihlášení do Unicorn Universe</li>
      <li>Přístup do vašeho kalendáře Google pomocí speciálního URL (detaily jsou uvedené přímo v aplikaci)</li>
      <li>Pokud chcete schůzky synchronizovat do jiného než primárního kalendáře, vyplňte ID tohoto kalendáře
          (ID je ve tvaru <tt>&lt;směs (téměř)náhodných znaků&gt;@group.calendar.google.com</tt> a najdete ho v nastavení kalendáře pod položkou Calendar ID)</li>
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
<h2>2012-03-10</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/17">ISS17</a> -
  Upraveno nastavování look &amp; feel pro konfigurační aplikaci.
  Chyba při nastavení look &amp; feel se ignoruje.
</li>
</ul>

<h2>2012-02-26</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/16">ISS16</a> -
  Doplněno ošetření systémové proxy. Pro korektní fungování je nutné použít Oracle/Sun JRE.
</li>
</ul>

<h2>2012-02-07</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/15">ISS15</a> -
  Opravena synchronizace neblokujících nových schůzek.
</li>
</ul>

<h2>2012-02-06</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/14">ISS14</a> -
  Opravena synchronizace schůzek, které nemají definované žádné místo.
</li>
</ul>

<h2>2012-02-05</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/11">ISS11</a> -
  Doplněna synchronizace neblokujících schůzek.
</li>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/12">ISS12</a> -
  Snížen minimální interval pro synchronizaci na 30 minut.
</li>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/13">ISS13</a> -
  Aktualizováno parsování HTML na aktuální verzi UU.
</li>
</ul>

<h2>2012-01-29</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/8">ISS8</a> -
  Opravena NullPointerException při synchronizaci s prázdným kalendářem.
</li>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/9">ISS9</a> -
  Doplněna možnost konfigurace vlastního kalendáře.
</li>
</ul>

<h2>2012-01-12</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/6">ISS6</a> -
  Opraveno ošetření timeoutů při komunikaci s UU, nyní by již synchronizace s UU neměla vytuhnout.
</li>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/7">ISS7</a> -
  Doplněno ošetření stavu Attention.
</li>
</ul>

<h2>2012-01-04</h2>
<ul>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/2">ISS2</a> - 
  Aplikaci je nyní možné instalovat i do adresáře s mezerou v názvu (takže si s chutí užijte Program Files).
</li>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/3">ISS3</a> - 
  Správnost přihlašovacích údajů do Unicorn Universe je kontrolována již při konfiguraci. Navíc pokud se zjistí neplatnost přihlašovacích údajů během synchronizace, vypíše se do logu rozumné chybové hlášení.
</li>
<li>
  <a href="https://github.com/pavel-zeman/UUGoogleSync/issues/4">ISS4</a> - 
  Entita &amp;amp; je při načítání dat z Unicorn Universe korektně přeložena na &amp;.
</li>
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
    Síťová komunikace - Všechna data jsou přenášena přes šifrované spojení (https), nemohou být tedy zachycena ani modifikována neoprávněnou osobou. 
    Jedinou výjimkou je ověření, zda vaše gmailová adresa může aplikaci používat. Pro tyto účely se šifrované spojení nepoužívá a vaše gmailová adresa je tak přenášena v čitelné podobě.
  </li>
  <li>
    Konfigurační data - Konfigurační data (soubor <tt>config/UUGoogleSync.properties</tt>) obsahují citlivé údaje. Konkrétně jde o vaše přístupové kódy do Unicorn Universe a 
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

<h2>Nastavil jsem si synchronizaci každých 15 minut, ale data se synchronizují pouze jednou za půl hodiny. Proč?</h2>
<p>
Minimální možný interval pro synchronizaci je 30 minut. Jakýkoliv kratší interval se ignoruje.
Důvodem je výše uvedené omezení na počet volání Google API a minimalizace zátěže na Unicorn Universe.
</p>

<h2>Je nutné spouštět nějakou aplikaci na mém vlastním počítači? Nemohl by synchronizaci provádět nějaký server?</h2>
<p>Mohl. Dokonce je to technicky velice jednoduché. Problém je ovšem v tom, že by na jednom místě byly přístupové kódy do Unicorn Universe pro všechny uživatele aplikace,
což považuji za relativně velké bezpečnostní riziko. Proto o této variantě zatím neuvažuji.</p>

<h2><a name="FAQ_LINUX">Běží aplikace i pod Linuxem, Mac OS X nebo jiným mým oblíbeným operačním systémem?</a></h2>
<p>
Aplikace je implementovaná v Javě SE a měla by tedy fungovat pod libovolným operačním systémem, pro který je k dispozici JRE verze 6 nebo vyšší.
Pro spuštění aplikace pod jiným operačním systémem než MS Windows nelze samozřejmě použít dodávané EXE soubory, ale je nutné použít přímo JRE a aplikační JARy.
Aplikace se potom spouští následujícím způsobem (jde o jediný příkaz, který je potřeba napsat na jeden řádek, nebo rozdělit podle pravidel používaného prostředí):
</p>
<p>
<tt>java -cp lib/commons-logging-1.1.1.jar:lib/google-api-client-1.6.0-beta.jar:lib/google-api-services-calendar-v3-1.3.1-beta.jar:lib/google-collections-1.0.jar:lib/google-http-client-1.6.0-beta.jar:lib/google-oauth-client-1.6.0-beta.jar:lib/guava-r09.jar:lib/httpclient-4.1.2.jar:lib/httpcore-4.1.2.jar:lib/jackson-core-asl-1.9.1.jar:lib/log4j-1.2.16.jar:config:lib/UUGoogleSync.jar -Xmx32m -Xms32m cz.pavel.uugooglesync.UUGoogleSync</tt>
</p>
<p>Pro korektní fungování uvedeného příkazu je potřeba dodržet následující:</p>
<ul>
<li>Všechny uvedené cesty jsou relativní. Příkaz je tedy potřeba spouštět z adresáře, kde je nainstalovaná aplikace UUGoogleSync.</li>
<li>
  Aplikace je spuštěna se 32 MB Javovského heapu, což by mělo obecně stačit.
  Pokud se objeví nějaká chybová hláška o heapu, zkuste heap zvýšit (jde o parametry -Xmx a -Xms).
</li>
</ul>
<p>Konfigurační aplikace se spouští analogicky. Pouze je potřeba na konci příkazu nahradit třídu <tt>UUGoogleSync</tt> za <tt>UUGoogleSyncConfigurator</tt>.</p>


<h2>Proč má taková kravina více než 3 MB?</h2>
<p>Samotná aplikace má méně než 50 KB. Zbytek jsou zejména knihovny Google API. Zkuste se tedy zeptat u Googlu.</p>

<h2>Aplikace nejde spustit, vypisuje chybu, nesynchronizuje schůzky nebo se chová jinak špatně. Co s tím?</h2>
<p>
Zkontrolujte, zda máte nainstalované JRE verze alespoň 6, případně toto JRE nainstalujte a zkuste to znovu.
Pokud ani potom aplikace nefunguje, zkuste zkontrolovat aplikační log (<tt>log/UUGoogleSync.log</tt>) nebo mi tento log pošlete. Určitě to nějak vyřešíme.
</p>

<h2>Přišel jsem o schůzky, které jsem měl v kalendáři Google před spuštěním synchronizace</h2>
<p>Aplikace je v alfa verzi a používáte ji na vlastní riziko, takže sorry (ale já ji používám už delší dobu a zatím je to bez problémů).</p>

<h2>Proč je ta ikona v systray tak ošklivá?</h2>
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