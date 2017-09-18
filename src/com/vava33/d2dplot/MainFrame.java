package com.vava33.d2dplot;

/**    
 *    TODO: Povar passar intensity[][] a arraylist de classe pixel. D'aquesta manera podriem emmagatzemar el COLOR/BW i descarregar pintaimatge
 *          S'ha fet la prova i es més lent.
 *  - TODO: DB editor (import other formats)?
 *    TODO: es podrien unificar les classes Punt...
 *    TODO: Fer una subrutina a pattern2D que ens torni el maxI pero sense considerar espuris... complicat
 *    TODO: fer "local" a pattern2D el saturValue
 *    
 *    NOTTODO: Passar tots els dialegs de MainFrame a STATIC -- MILLOR NO
 *    Addlat: 1) posar a la quicklist un compost de la database (DONE)
              2) importar a la quicklist un LAT, HKL, etc... (PARTIALLY DONE)
              3) generar les reflexions a partir d'una cella + grup espacial (TODO)

*** PROGUARD I EXPORT: Al final he fet:
*   - exportar jar EXTRAIENT les llibreries
*   - ofuscar amb proguard ignorant els warnings!
*   - aixi obting un jar que ocupa molt poc... pero funciona!
 *  - TODO: ABANS DE GENERAR EL JAR -- canvi numero versio a global, posar overrideLogLevelConfigFile a False

 * Current:
 *  - *** es l'ultim fet amb ordinador antic compliance 1.6 *** 
 *  - Neteja finestra PKsearch (simplificacio) 
 *  - TODO: mirar si l'ultim pas de findpeaks es pot fer amb un patt2dzone i mirar llista pixels (potser es més rapid...)
 *  - Canviat IntRad a writePCS perque donava el valor de la zona (longitud radial) i hauria de ser el radi del pic en pixels.
 *  
 * 170616:
 *  - TODO: posar label de X peaks found! a la dreta del frame sino mou els botons
 *  - TODO: a l'obrir peaksearch podria anar calculant el fons en 2n pla...
 *  - CAMBIAT EL METODE DE DETECCIÓ DE PICS, ARA S'UTILITZA EL CALCUL DEL FONS AVSQ
 *  - Afegit a sumaPatterns la possibilitat de restar un fons
 *  - CALCUL FH2 donava FH... arreglat, canviat una mica les formules
 *  - He fet canvis a calcul mean/sd a l'obrir una imatge (quan hi havia molts saturats petava) --- potser no hauriem de considerar saturats?
 *  - Canviat el llindar a findpeaks (mes obertura angular als trams i utilitzar altre cop la mitjana de tram) ... fact 6.0, veins30, minpx8 funcionen prou be
 *  - TODO: fer unsa subrutina copy instr parameters from anoter patt2D... (per evitar capçaleres corruptes i utilitzar la primera d'una serie)
 *  - TODO: Obrir OUT (corregit index imatge 1a linia) i escriure OUT donada una serie de imatges
 *  - TODO: revisar manual (fer una nova versio)
 *  - TODO: video sequencies imatges (halfdone, revisar, millorar i integrar al programa)
 *  - TODO: es pot posar a export PNG una opcio que et calculi l'escala per mida real de la imatge MANTENINT els dibuixets
 *  
 *  170516
 *  - DONE: obrir OUT cercapics, fer que show OUT peaks al cambiar imatge (potser obrir out i guardar info d'imatge? o anar llegint...). TODO: corregir index imatge quan a l'OUT ja estigui arreglat
 *  - Afegit pixelList a Patt2Dzone
 *  - DONE: he fet que nomes es pintin magenta els punts I<0 si tenim el paint exz activat (pot agradar o no...), tambe que es pintin els saturats
 *  - DONE: llegir TIF o CBF xaloc, a tavés de imageJ
 *  - DONE: corregir imagepanel linia 469 (TODO) -- no se perque tenia la condicio que tambe entres si s'estava calibrant
 *  - DONE: aplicar calibracio ja funciona i no es duplica imageParametersDialog
 *  - DONE: canvi finestra integracio plot1D per d1dplot -- ELIMINAT JFREECHART DEPENDENCE
 *  - DONE: conversio fit2d a d2d no funciona be
 *  - DONE: mida finestra minima es massa gran! afegida opcio i posades jscollbars
 *  - No llegia alguns edf generats, he canviat els scanners per: Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
 *  - SavePNG amb factor i amb tota la parafarnalia (DONE hauria de mantenir el fet de guardar la imatge tal qual)
 *  - canviats getParent per getAbsolutePath
 *  - correccio a fitImage al calcul de scaleX
 *  - Afegida lectura/escriptura tilt/rot del/al header edf
 *  - Afegida comprovacio de "AUTO" al fer la integracio d'un sol pic (clicant)
 * 
 * 
 * 161215dist:
 *  - Corregida zona arc a 180 full circle
 *  - NOTDONE, NO FUNCIONA: Canvi filosofia isInExZone. Al canviar pixels ExZ poblarem una llista de pixels en exz a pattern2D i despres nomes mirarem si un pixel esta a la llista.
 *    Lo ideal seria canviar array YX per classe pixel... pero es un canvi major
 *    ATENCIO: HO HE PROVAT PERO NO HA FUNCIONAT MES RAPID, S'HA CANVIAT A HASHSET LA LLISTA DE PIXELS EN ARCZONE PER AUGMENTAR VELOCITAT
 *  - ImagePanel, separats if (inExz and showExz)
 *  - Canvis varis a ExZones:
 *     - Reestructuracio.
 *     - Al fer write MASK.BIN també s'escriu fitxer EXZ automaticament.
 *     - Lectura MASK.BIN i copia dels pixels
 *     - Correccio updateArcList quan acaba de dibuixar arc (s'actualitzava la poly)
 *     - DONE: fix slow arc showing, canviant a HashSet els pixels en arczone millora molt
 *  - Afegit a Calib_dialog writeCAL linia CALFILE (i la data)
 *  - Solucionats alguns problemes de macro mode (problemes al fer l'execucio en linux amb script per spock):
 *      -- es duplica el path quan s'entra la imatge d'entrada amb el path complert
 *      -- el fitxer d'inp de rint s'ha de fer que el busqui per defecte al mateix path que la imatge d'entrada
 *      -- despres d'una calibracio posar la linia de comandaments que permeti integrar directament
 *      -- cal afegir linia I vs 2theta a la capçalera per tal que plmany consideri 2theta
 *  - Correcció a imagePanel affinetransform: if (originX>0)offsetX = originX; (i no pas offsetX+originX), i també per la Y
 *    No funcionava bé la traslacio de la imatge quan estavem desplaçats en positiu del 0,0

 * 161118dist:
 *  - Neteja
 *  - Implementat el Mouse Free Paint Excluded Zone
 *  
 * 161118:
 *  - DONE: MASK file ha de ser BIN per força, ja que es l'unic que accepta -1 --- CORREGIR EL BIN QUE NO FUNCIONA BE
 *  - DONE: Fix overwritting of custom db file, a mi em sembla que funciona bé... he posat un sleep abans de tancar per tenir temps d'escriure-ho sino quedava buit
 *  - DONE: actualitzar manual
 *  - DONE!: crear d2_ovedf.py que segueixi el mateix criteri que d2dplot
 *  - DONE!: arguments consola, crear classe que ho faci. CALDRA MODIFICAR TTS_GUI afegint opcio -macro
 *  - Afegides zones excloses en forma d'arc i circular detector (i a fitxer exz i a fitxer d2d), també salvar mask.bin en altres formats, (ex.edf)
 *  - ArcZone anava molt lent el contain i al final he fet llista de pixels que nomes es calculi un cop.
 *  - Afegida comprovacio 2theta a arcNpix (contava pixels a tota la zona quadrada)
 *  - PRINT ALL THE OPTIONS USED AT THE BEGGINING TO SEE WHAT HAS BEEN APPLIED
 *  - Acabar d'arreglar el color... he fet varies proves pero m'he quedat amb un simplificat al final que no esta mal
 *  - DONE: Posar a tots els dialogs/frame MainFrame -- GRAN CANVI ... al final ho he fet amb ImagePanel... no se que es el millor
 *    Aixo ho he fet per poder cridar actualitzar vista i evitar que paintcomponent estigui fent bucle tota l'estona
 *  - Auto slider value canviat, mes intuïtiu ara
 *  - DONE: a closePanels (o update patt2d, etc..) no tanca sino actualitzar, tipus: this.pksframe.updateData(this.patt2D); tots tenen un inicia()
 *  
 * 161107: (versio bona abans de netejar)
 *  - DONE: crear un logger unic a cada classe per veure d'on venen les coses (pot ser un vavalogger per tenir els metodes, pero crearlos amb el NOM de la classe).
 *  - tret genlab6 de calib
 *  - show HKL a inco auto on
 *  - Arregrlat write mask.bin (agafa filename i no directori per guardar-lo)
 *  - About click es molest-- arreglat, afegit menu
 *  - DONE: millorar eficiencia, paintcomponent esta cridant-se continuament, posar una comprovacio al principi de si ha canviat algo?
 *    Tret el repaint de dins paintcomponent. Ha fet falta afegir repaints despres de totes les operacions! (mouse button, etc...), nou metode public actualitzaVista
 *    Potser caldria millorar PintaImatge
 *  - DONE:Integracio implementar subadu, radial bins, ... etc... i arreglar una mica l'estetica
 *  - Calib trec el tema de reject outliers i simplifico tot
 *  - AL FINAL HE CANVIAT LA CORRECCIO DE L'ORIGEN. A LA VERSIO ANTERIOR HI HA UNA SOLUCIO, MOVIMENT AMB ENTERS. AL FINAL HO HE FET AMB AFFINE TRANSFORM AL DIBUIXAR
 *    LA IMATGE QUE PERMET COMENÇAR A LA MEITAT DELS PIXELS. AIXI TAMBE RECUPERO EL MOUSEDRAG COM EL TENIA.
 *  - Canvi FastMath.rounds per (int) a tot el programa, ja que aixi es com tenim descrita la imatge...
 *  - DONE: Al clicar a image parameters desde lab6 calibration et mostra una d'antiga si has canviat d'imatge entremig -- ARREGLAT
 *  - DONE: Millorar LaB6 calibration. Implmentar minimització. -- era un global todo
 *  
 * 161103:
 *  - Implementat minimitzacio parametres instrumentals segons el dspacing. Sembla que funciona. Deixo les dues minimitzacions en aquesta versio, deixare nomes la dspacing a la seguent
 *  - l'addicio de l'int a get pixel NO era solucio al coincidir visualment, es problema del MOVE origin que s'ha d'encaixar amb moviments enters de pixels
 *    HE HAGUT DE CREAR UN CORRECT_ORIGIN I JUGAR AMB EL CALCRECTDINSFRAME PERQUÈ TOT QUADRI
 *  - Fix 1D integration. TD:implementar subadu, radial bins, ... etc... i arreglar una mica l'estetica
 *  - keep calibration info for the session
 *  - add/remove peaks directly without previous calculation on find peaks
 *  - Una mica de neteja pero falta molt.. mes amb tot el que he afegit i corregit
 *  - millorada deteccio d'elipses (punts) ara es busca en una zona quadrada el pixel de max intensitat en cada pixel de la linia radial, permet augmentar el arcpix per defecte.
 *  
 * 161102:
 *  - COMENTAT el RESETVIEW a setImagePatt2D dins IMAGEPANEL per mantenir la zona al canviar d'imatge (FF)
 *  - IMPORT TABLE AT PEAKSEARCH
 *  - Fet que visualment els pixels coincideixin amb els num, AFEGINT (int) a getPixel... revisar? 
 *  - Sembla que he arreglat el tilt/rot convention, afegit també conversió a fit2D.
 *  - Trec el global rot (force rot, etc...)
 *  - Canvi calc2T igualant amb la integració edf python que he fet... bé, diferent ja que cosidero les MEVES convencions.
 *  
 * 161014: (guardo aquesta versió perque tot va bé i vull fer neteja de la calibració)
 *  - !!! Fent proves de calibracio:
 *      - global rot regr no funciona be (mirar dibuixos lliberta), el eix comença a 0, llavors va de 180 a 0 i torna de 180 a zero conforme va girant.
 *        no dona en cap cas el gir respecte la vertical degudament.
 *      - cal mirar que passa perque la 2theta i el dibuix de les elipses no es corresponen amb la calibració ni amb la generacio 
 *        de les imatges lab6 (que sembla ben feta veient els numeros ja que es fa a partir de les 2theta), s'ha de mirar on hi ha el mismatch.
 * 
 *  - Fet dialeg per generar imatges LAB6 per debug tilt/rot
 *  - Eliminat un updatePatt2D, ara nomes queden obrir file i obrir pattern 
 *    (EXTODO: mirar totes les ocurrencies de updatePatt2D i UNIFICAR)
 *  - auto add BS excluded zone?
 *  - Batch integrate
 *  - Eliminat metode REDUC només deixo el D2DPLOT.
 *  
 * 161010:
 *  - A AQUESTA VERSIÓ ENCARA CONVIUEN EL METODE REDUC I EL D2DPLOT. Aquest últim funciona bé, 
 *    el reduc no l'he provat amb l'axinita, però suposo que caldria corregir els probemes de 
 *    pixels sense integrar.
 *  - Auto radial widh and azim aperture millorades considerant saturats
 *  - Cerca pics millorada (zones saturades)
 *  - Correccio LP corr amb lo del jordi
 *  - Busca fitxer EXZ amb nomes prefix
 *  - Implementació del mètode REDUC per integració
 *  - NOTODO: ATENCIO CALDRA canviar integratePK per integratePK_JR quan funcioni
 *
 * 161006:
 *  - TD: Caldria Neteja per distribucio
 *  - NOTODO: llegir HKLinco?
 *  - Mogut deleteOutliersFromList de FileUtils a calib_dialog perque vavautils ocupaven massa
 *  - PROVA d'OBRIR directament un pattern i un SOL des del command line
 *  - Fet que tant els parametres com el pksearch no es tanquin al canviar imatge
 *  - Llegir nova versio del SOL amb val rotacio
 *  - Al tornar a obrir peaksearch es posen be els defaults als camps de text
 *  - Flexibilitat el next/previous image
 *  - Classe Peak ara conté TOT.
 *  - Separat update table dels calculs
 *  - Adaptat per llegir la nova versió del SOL amb la linia FROT
 *  
 * 160926:
 *  - TD: import PCS a find peaks? o no cal? el tema es que s'importa sense la info dels veins,etc...
 *  -       S'hauria d'arreglar tot aixo del PCS, fullRowInfo, Peak, OrientSol, etc... UNIFICAR UNA MICA
 *  - Sembla que funciona bé i tot el que volia està implmentat... cal fer versio neteja i dist
 *  - Write MASK.BIN implementat
 *  - Unificacio PCSrow a RowFullInfo per poder escriure TOT.
 *  - Posat patt2Dzone dins a Peak, eliminat array de zones a PKsearch
 *  - Pas metodes de PKsearch a Patt2D
 *  
 * 160923:
 *  - Funciona bé. Passo a nova versió per posar Patt2Dzone dins a Peak (implica forces canvis)
 *  - Molts canvis i millores... ImgOps nous metodes calcul pixels zona, Yarc triplicada, ... 
 *  - Current tol2theta a pksearchframe ha de retornar el seleccionat!!
 *  - Corregit getMinStepsize
 *  - Canviat calcul pixeltolerance a YarcTilt
 *  - Creat YarcTilt amb tol2tpixels
 *  - Opcions pic/integracio, p.e. canvi radwidth de 2theta a pixels
 *  - Canvis a deteccio de pics, proves de delsig auto,etc... ara tambe es busca el centre millor,etc...  
 *  - Unificacio taula/PCS (ara nomes es calcula un cop)
 *  - PCS: Ymax se li resta el fons.
 *  
 * 160921:
 *  - SEMBLA que la integracio funciona bé, faltarà unificar taula/PCS, afegir opció "show integration areas for all spots",
 *    write MASK.BIN, auto 2thetaTol i azimangle,...
 *  - Faig un canvi a la integracio, els bkgpt no es consideren SI superen la Ymean
 *  - Generacio PCS, molta cosa feta. Nou metode get pixel from azim and t2
 *  - Creada classe PixelExtended dins de Pattern2D
 *  - Al salvar un BIN els saturats passaven a ser màscara (faltava escalar scaI2)
 *  - Afegit a calcMean (Pattern2D) el tractament de PIXELS-->PICS saturats
 *  - Ask output folder in batch convert
 *  - Afegides dues opcions mes al fitxer config sobre puntsSolucio (fill i mida stroke)
 *  - CORREGIT FACTOR ESCALA AL SUMAR IMATGES (MULTIPLICAVA I HAVIA DE DIVIDIR)
 *  - Corregida la seleccio i mostra de zones excloses
 *  - Afegida una neteja dels moduls mes exhaustiva al reset (canvi imatge)
 *  - MANUAL (o QUICKGUIDE ALMENYS....)
 *  - neteja output log (tambe tots els printstacktrace nomes si esta en mode debug)
 *  - Neteja General del codi
 *  - Added scan parameters (omegas + acqtime) to BIN file
 *  - Save config: Afegits valors variables "static" que es considerin utils (duplicats a d2dplot_global amb valor defecte a la classe original)
 *  - Passar Parametres D2Dplot_global i completar fitxer configuracio
 *  - Neteja output CALIB i perfilar funcionament
 *  
 * 151118 (major changes)
 *  - CHECK PATHS D2DCONFIG,DB... add full paths to config file for DBs to be able to correct manually.
 *  - Treballat en LaB6 calib, pero encara no funciona del tot be...
 *  - Dinco extract intensities (fent servir pksearch!)
 *  - Logo ALBA a ABOUT i ara es llegeix about.html per mostrar en un editpane
 *  - Added after every (or most of) filechooser save workdir to recover for further file openings (same folder)
 *  - DB editor import hkl
 *  - posar conveni tilt rot als parametres (per poder convertir els d'altres programes)
 *  - Subtract images.
 *  - Opcio custom dist, wavelength, exz when batch converting
 *  - Quicklist llegeix LATs que estiguin a la carpeta del programa o bé entrades de la DB que s'hagin afegit a la quicklist.
 *  - Fitxer config. Es llegeix a l'obrir i es guarda al sortir. Es poden afegir els paràmetres que es cregui convenients.
 *    Al sortir, es pregunta si guardar la QL en cas que hagi canviat.
 *  - Canvi funcionament work_sol, ara es afegir pics manualment. La llista mostra tots els spots. A més ara les llistes tracten
 *    directament amb els objectes (fent servir el toString).
 *  - Implementat HPcalc ... pot millorar-se es molt simple
 *  - Implementat Peaksearch
 *  - Dinco permet borrar pics, etc..
 *  - Revisat D2Dsub
 *  - Comprovades unitats a corrLP (estaven malament)
 *  
 * 151029 (major changes)
 *  - PDDatabase gestiona la DB complerta i la QuickList
 *  - SearchDB millorat, Ellipses implementades a tot el relacionat amb DB, quicklist/DB ben posat
 *  - Unificacio lat_data i pdcompound a PDCompound
 *  - AfegiT scan info als parametres (omega_ini omega_fin time) -- lectura/escriptura de l'EDF/d2d..
 *  - Renombrats metodes ImgFileUtils (Read/Write,.. ara tots coherents)
 *  - Classe pels parametres globals D2Dplot_global
 *  - Imagepanel busca què ha de pintar a les diferents parts del programa. No obstant hi ha unes variables booleanes per
 *    forçar que no pinti alguna cosa, per si es vol fer servir en algun lloc apart.
 *  - Imagepanel pot tenir la info a baix o al costat segons una variable booleana
 *  - Sistema de menus, netejat el mainframe de butons
 *  - DINCO/XDS dialog apart amb les seves opcions
 *  - ExZones threshold
 *  - Auto contrast com a opció
 *  - Sum images i batch convert images finalitzats, simples amb progress i utils.
 *  - Neteja i unificació formats lectura/escriptura a classe ImgFileUtils. Facil afegir i treure sense haver de tocar cap altra part
 *  - Pas dels parametres generals a D2Dplot_global
 *  - this... static, no estatic... mirar d'arreglar-ho
 *  
 * 201015 (major changes)
 *  - change of vavalogger
 *  - Yarc passat a tractar com a ellipse
 *  - Neteja ImgOps, integracions antigues borrades
 * 
 * Plot2dcurrent (last with the name)
 *  - CALIB/INTEGRACIO -- finalitzacio calib (i neteja)
 *  - Escriptura EDF,IMG,D2D,...
 *  - He fet una copia local de vavalogger perque encara compili aquesta versio, pero la següent (amb canvi de nom
 *    inclòs a d2dplot) ja no fa servir aquest sistema.
 *  - ULTIMA VERSIO AMB NOM PLOT2D. LA SEGUENT SERA D2DPLOT I FARE (PER FI ESPERO) UNA NETEJA GENERAL.
 *  - CALCUL 2theta considerant tilt/rot correcte, LA INTEGRACIO FUNCIONA PERO LA CALBIRACIO ENCARA NO...
 *    S'haurien de minimitzar les ellipses ajustades amb les "pintades" utilitzant el tilt/rot/centre com a variables a afinar.
 *  
 * 151009
 *  - Still pending todos from 151002
 *  - a mitjes..Neteja de tot (unificació classes, nova ImagePanel, etc..) -> anar cap a versió definitiva
 *  - Poligon per EXZ. (nou funcionament clics)
 *  - Funciona el fit d'ellipses, la calibració i la integració amb aquesta informació
 *  - Added tilt info to patt2D parameters
 * 
 * 151002
 *  - USE OF INTEGER DATA TYPE INSTEAD OF SHORT (SIGNED). MAXIMUM WILL BE 65536 instead of 32768 
 *    (except for the BIN format that will keep the same signed shorts with max 32768)
 *  
 * 150902:
 *  - LAT file custom no afegia a la llista de lats
 *  - Open SOL accepta fitxers XDS (SPOT.XDS)
 *  - Funciona el zip DB des de l'eclipse però no quan faig el JAR.
 *  - Nou criteri per posar l'slider contrast al carregar imatge
 *  - Canvi el tractament dels LAT files (HKL_data changed to LAT_data), ara es poden carregar de nous
 *  - Base de dades de compostos i cerca per d-spacing
 *  - save peaks for dicvol (ordered!)
 *  
 * 150302:
 *  - Afegida l'opció de pintar anells de compostos (fitxers locals .LAT amb primera linea de cel·la)
 *  - Arreglat una mica l'UI per millorar linux
 *  - Arreglat el lpcorr (amb metode JR) falta testar-lo
 *  - botons next, prev 
 *  
 * 141013:
 *  - in ImagePanel.PaintComponent mogut el repaint() a dins el if, ja que sinó a l'obrir sense imatge tot anava molt lent
 *  - working on: frame workSOL (clicar i editar el mes proper). Ha fet falta canviar bastanta cosa. He implementat els
 *    m�todes de cerca Punt proper a ImagePanel. He afegit par�metres "click" a PuntSolucio. Far� que els punts clicats
 *    no es mostrin o b� s'ACTUALITZIN amb les coordenades clic (pero potser hauria de canviar el color o algo perqu�
 *    es noti). A PkList_dialog poso que segons qu� hi hagi seleccionat es mostri una llista o una altra i permeto borrar
 *    linies o tota la llista (per� borrar de VERITAT els pics seleccionats).
 *  - working on: integracio radial, ellipse fitting, batch processing
 *  - Added Logger
 * 
 * 140404
 *  - Canviat tots els Math per FastMath (org.apache)
 *  - Arreglada la sostracci� del vidre (no funcionava)
 *  - Ara es pot entrar com a par�metre el directori de treball o un fitxer que s'obrir� automaticament
 * 
 * 140310
 *  - Corregit problema crida Yarc2 on s'acumulava l'amplada. Ara tot funciona b� i prou r�pid.
 *  - Canviades totes les crides a Yarc per Yarc2.
 *  - S'ha creat Yarc2 que funciona b�, ja no dona errors de pixels omesos com Yarc i a m�s els par�metres d'entrada
 *    angle i amplada estan en unitats GRAUS i PIXELS respectivament, molt m�s intu�tiu. He afegit un m�tode a Pattern2D
 *    que calcula un factor a multiplicar a angle i amplada per compensar l'efecte de l'angle (TODO: revisar que m'agradi)
 *  - Afegits throws a interruptedException als m�todes de treure el fons (es podria mirar d'aplicar fins on s'hagi arribat...)
 *  - Treiem els diagrames del D2Dsub_frame, treballarem sobre la imatge oberta. Aix� ha comportat molts canvis
 *  
 * 140227
 *  - FileUtils i LogTextArea importats del projecte vava --> comprovar que tot vagi b�!
 *  - Implementaci� substracci� de fons directament des de JAVA  -> Funciona la primera prova per� cal readequar-ho tot!
 *    sobretot la classe D2Dsub_frame i tota la gesti� de les imatges i els fitxers
 *  - Implementaci� de mod_2ddata (fortran) a Java (ImgFileUtils, Pattern2D, exZones)
 *  
 * 140127
 *  - Contrast   
 *  - Canvi a recalcScale a pattern2D que dividia per l'escala quan havia de multiplicar
 *    
 * 131008
 *  - Ordenem arraylist orientSolucio segons Fsum abans d'omplir la llista
 *  
 * 130923
 *  - Cal reestructurar tot lo de les zones excloses per acceptar trapezoides.
 *  - CurrentRect es seguir� fent servir per Calibraci�, per� generar� un de nou que sigui CurrentShape que ser� el que
 *    treballar� amb ExZones i tot es definir� amb Shape.Polygon. Aleshores tamb� cal canviar la crida a editquadrat per
 *    la crida a una nova subrutina editpoligon quan s'esta definint les zones excloses i es clica.
 *  - Save BIN ara tamb� considera Y0toMask (afegit a patt2D el Y0toMask option)
 *    
 * 130918
 *  - Obertura fitxers EDF (ALBA)
 *  - ImagePanel, prova de flexibilitzar contrast (posant dinamics max, min)
 *  
 * 130611
 *  - Adaptacio D2Dsub nova versio amb m�s opcions
 *  - Canvi noms opcions D2Dsub
 * 
 * 130604
 *  - TREC el CODI de D2Dsub
 *  - S'ha fet que abans de guardar el BIN es faci un REESCALAT rellegint si cal les intensitats originals per tal d'aplicar
 *    les possibles zones excloses que s'han afegit
 *  - Poso a FileUtils els DecimalFormats i el locale
 *  - Faig que no apliqui l'escala al mostrar la intensitat
 *  - Faig que consideri les intensitats ZERO pel calcul de minI
 *  - Canvi color lletra consola a groc
 *  - Obertura fitxers bin antics? -> deteccio automatica segons num de bytes (cap�alera de 20 (old) vs 60 (new) bytes)
 * 
 * 130601 ** AQUESTA �S LA VERSI� CONSIDERADA "ACABADA" PER 1306 **
 *  - Hem fet que el fortran escrigui molts !! per omplir el buffer de consola i aix� poder mostrar els missatges que volem
 *    i QUAN volem per pantalla. Simplement ignorarem les linies que comencin per !!!!! (a veure si funciona)
 *  - TextArea output amb boto dret.
 *  - Neteja general i preparacio per distribu�r.
 *  
 * 130530
 * - Escrits els HELP dels diferents dialogs.
 * - Reset IMATGE complert
 * - Al tancar finestres es deseleccionen checkboxes que afecten el que es mostra a la imatge (calibracio i exZ son exclusius)
 * - SaveBin contempla zones excloses (es guarden a pattern2d) i s'ha passat a FileUtils (igual que savePNG)
 * - Acabada la implementacio de zones excloses (writeEXZ,etc..)
 * - Determinacio de les zones excloses passa a ser responsabilitat del programa principal. 
 * 
 * 130529 
 * - inici implemetnacio d2DExZones 
 * - Centratge imatge al panell amb reset view
 * - Neteja fitxers i opcio clean up.
 * 
 * 130528-2 
 * - Petites correccions de varis errors de nullpointers, access directe a camps privats, etc...
 * - Redissenyat el d2Dsub, ara tot es fa a la mateixa finestra. Ja funciona tot menys zones excloses.
 * - Classe fileUtils, amb operacions fitxers i LECTURA FITXERS DADES
 * - Canvi filosofia: Tota la responsabilitat de representacio (mouse, etc..) a Imagepanel (per reaprofitar-la a altres llocs)
 * 
 * 130528
 * - Execucio del d2dsub des  de D2Dsub_dialog
 * - Canvi workdir a string i afegides variables static globals per execucions
 * - Creacio classe output text area (JtxtAreaOut)
 * 
 * Changes (130523):
 * - Finestra FONS
 * - CANVI FORMAT BIN ACTUAL (cap�alera 60) per open i save. 
 * - Del IMG ara es llegeixen tots els parametres.
 * 
 * Changes (130517-21):
 * - Intentat aproximar millor els pixels en pantalla
 * - Calibraci� amb el�lipse dibuixada i ara es poden triar quins anells s'utilitzen per calibrar
 * 
 * Changes (130515):
 * - UI amb parts resizables (Splitpanes)
 * - LaB6 calibraci� centre, distOD i Lambda?.
 * 
 * Changes (130514):
 * - Classe Pattern2D, canvi a tot arreu.
 *  
 * Changes (130510,13,14):
 * - Format sol, 1a linia NPEAKS FSUMVAL
 * - Canvi funcions bot� esquerra i central
 * - SetParams -> nova finestra amb tots els parameters
 * - Canvi centrX,centrY a float
 * - Reacalcul dels PuntCercles si es canvia el cercle
 * - Llista pics a dialog apart
 * - labels HKL a les reflexions solucio (showHKLsol)
 * - Passat tot a pixels fraccionaris (getPixel, getFrameFromPixel...)
 * - Obrir fitxers de bruker GADDS .gfrm
 * 
 * Changes (130318):
 * - obre el format SOL nou (shape and all)
 * - Moguda la comprovacio de parametres entrats dins la subrutina d'entrada de parametres instrumentals
 * - tret el color gris fosc de la representacio de solucions
 * 
 * Old changes:
 * - CANVI FILOSOFIA GENERAL DE REPRESENTACIO I PER APROXIMAR MILLOR LES COORDENADES... ara es
 *   pot moure i ampliar la imatge millor, es representa m�s lliure.
 * - NETEJA general, tretes les opcions d'utilitzar Enters per guardar la imatge o de fer servir cap�alera antiga
 *   si es vol, fer servir una version anterior (130219-2)
 * - Opcio d'entrar informacio per mostrar 2T a sota
 * - Valors defecte JSlide contrast (aplicat factorContrast)
 * - Creacio classe OrientSolucio i reestructuracio de tota la representacio de solucions de forma
 *   que es poden obrir mes d'una alhora.
 * - Zoom amb bot� dret del mouse i movent.
 * - Creacio classe puntCercle i canvi dels m�todes de ImagePanel que l'afecten
 * - recorda el directori (i se li pot passar com a par�metre l'inicial)
 * - Opcio de cap�alera amb integer*4
 * - Considerem valors de zero pel minI
 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ArgumentLauncher;
import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.PDCompound;
import com.vava33.d2dplot.auxi.PDDatabase;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.SystemInfo;
import com.vava33.jutils.VavaLogger;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.miginfocom.swing.MigLayout;

import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.border.LineBorder;
import javax.swing.ScrollPaneConstants;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 4368250280987133953L;
    
    private static int def_Width=1100;
    private static int def_Height=768;
    
    private static VavaLogger log;
    
    private Pattern2D patt2D;
    private boolean fileOpened = false;
    private File openedFile;
    

    private ImagePanel panelImatge;    
    private D2Dsub_frame d2DsubWin;
    private D2Dsub_batch d2DsubWinBatch;
    private Calib_dialog calibration;
    private ExZones_dialog exZones;
    private About_dialog p2dAbout;
    private Pklist_dialog pkListWin;
    private LogJTextArea tAOut;
    private IntegracioRadial irWin;
    private DB_dialog dbDialog;
    private Dinco_frame dincoFrame;
    private Param_dialog paramDialog;
    private HPtools_frame hpframe;
    private PKsearch_frame pksframe;
    
    private JButton btn05x;
    private JButton btn2x;
    private JButton btnMidaReal;
    private JButton btnOpen;
    private JButton btnResetView;
    private JButton btnSaveDicvol;
    private JCheckBox chckbxIndex;
    private JLabel lblOpened;
    private JPanel contentPane;
    private JPanel panel_all;
    private JPanel panel_controls;
    private JPanel panel_opcions;
    private JPanel panel_stat;
    private JScrollPane scrollPane;
    private JSplitPane splitPane;
    private JSplitPane splitPane_1;
    private JButton btnNext;
    private JButton btnPrev;
    private JPanel panel_2;
    private JCheckBox chckbxShowRings;
    private static JComboBox combo_LATdata;
    private JButton btnDbdialog;
    private JPanel panel_3;
    private JButton btnAddLat;
    private JSeparator separator_1;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmAbout;
    private JMenuItem mntmOpen;
    private JMenuItem mntmSaveImage;
    private JMenuItem mntmExportAsPng;
    private JMenuItem mntmQuit;
    private JCheckBox chckbxColor;
    private JCheckBox chckbxInvertY;
    private JMenu mnGrainAnalysis;
    private JMenuItem mntmDincoSol;
    private JMenuItem mntmLoadXdsFile;
    private JMenuItem mntmClearAll;
    private JMenu mnImageOps;
    private JMenuItem mntmInstrumentalParameters;
    private JMenuItem mntmLabCalibration;
    private JMenuItem mntmExcludedZones;
    private JMenuItem mntmBackgroundSubtraction;
    private JMenuItem mntmRadialIntegration;
    private JMenuItem mntmFindPeaks;
    private JMenu mnPhaseId;
    private JMenuItem mntmDatabase;
    private JMenuItem mntmSumImages;
    private JMenuItem mntmBatchConvert;
    private JMenuItem mntmHpTools;
    private JPanel panel;

    //sumimages
    ProgressMonitor pm;
    ImgFileUtils.batchConvertFileWorker convwk;
    ImgOps.sumImagesFileWorker sumwk;
    private JButton btnInstParameters;
    private JSeparator separator;
    private JPanel panel_1;
    private JButton btnRadIntegr;
    private JButton btnTtsdincoSol;
    private JMenuItem mntmSubtractImages;
    private JCheckBox chckbxPaintExz;
    private JButton btnPeakSearchint;
    private JMenu mnHelp;
    private JMenuItem mntmManual;
    private JMenuItem mntmFastopen;
    private JMenuItem mntmScDataTo;
    
    public static String getBinDir() {return D2Dplot_global.binDir;}
    public static String getSeparator() {return D2Dplot_global.separator;}
    public static String getWorkdir() {return D2Dplot_global.getWorkdir();}
    public static void setWorkdir(String wdir) {D2Dplot_global.setWorkdir(wdir);}
    
    /**
     * Launch the application. ES POT PASSAR COM A ARGUMENT EL DIRECTORI DE TREBALL ON S'OBRIRAN PER DEFECTE ELS DIALEGS
     * 
     */
    public static void main(final String[] args) {
        
        //first thing to do is read PAR files if exist
        FileUtils.detectOS();
        D2Dplot_global.readParFile();
        
    	//LOGGER with the read parameters from file
        log = D2Dplot_global.getVavaLogger(MainFrame.class.getName());
        System.out.println(log.logStatus());
    	    	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if(UIManager.getLookAndFeel().toString().contains("metal")){
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");    
            }
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java metal
            
        } catch (Throwable e) {
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error initializing System look and feel");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.inicialitza();
                    //AQUI POSO EL ARGUMENT LAUNCHER
                    D2Dplot_global.printAllOptions("info");
                    ArgumentLauncher.readArguments(frame, args);
                    if (ArgumentLauncher.isLaunchGraphics()){
                        frame.showMainFrame();
                    }else{
                        log.info("Exiting...");
                        frame.dispose();
                        return;
                    }
                } catch (Exception e) {
                    if (D2Dplot_global.isDebug())e.printStackTrace();
                    log.severe("Error initializing main window");
                }
            }
        });
    }

    public void showMainFrame(){
        this.setLocationRelativeTo(null);
        this.setVisible(true);    
    }
    
    /**
     * Create the frame.
     */
    public MainFrame() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                do_this_windowClosing(e);
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/img/Icona.png")));
        setTitle("d2Dplot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1129, 945);
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        mnFile = new JMenu("File");
        mnFile.setMnemonic('f');
        menuBar.add(mnFile);
        
        mntmOpen = new JMenuItem("Open Image");
        mntmOpen.setMnemonic('o');
        mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mntmOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmOpen_actionPerformed(e);
            }
        });
        mnFile.add(mntmOpen);
        
        mntmSaveImage = new JMenuItem("Save Image");
        mntmSaveImage.setMnemonic('s');
        mntmSaveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mntmSaveImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmSaveImage_actionPerformed(e);
            }
        });
        mnFile.add(mntmSaveImage);
        
        mntmExportAsPng = new JMenuItem("Export as PNG");
        mntmExportAsPng.setMnemonic('e');
        mntmExportAsPng.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmExportAsPng_actionPerformed(e);
            }
        });
        mnFile.add(mntmExportAsPng);
        
        mntmSumImages = new JMenuItem("Sum Images");
        mntmSumImages.setMnemonic('s');
        mntmSumImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmSumImages_actionPerformed(e);
            }
        });
        mnFile.add(mntmSumImages);
        
        mntmBatchConvert = new JMenuItem("Batch Convert");
        mntmBatchConvert.setMnemonic('a');
        mntmBatchConvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmBatchConvert_actionPerformed(e);
            }
        });
        
        mntmSubtractImages = new JMenuItem("Subtract Images");
        mntmSubtractImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmSubtractImages_actionPerformed(e);
            }
        });
        mntmSubtractImages.setMnemonic('u');
        mnFile.add(mntmSubtractImages);
        mnFile.add(mntmBatchConvert);
        
        mntmFastopen = new JMenuItem("Fast Viewer");
        mntmFastopen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmFastopen_actionPerformed(e);
            }
        });
        mnFile.add(mntmFastopen);
        
        separator = new JSeparator();
        mnFile.add(separator);
        
        mntmQuit = new JMenuItem("Quit");
        mntmQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmQuit_actionPerformed(arg0);
            }
        });
        mntmQuit.setMnemonic('q');
        mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        mnFile.add(mntmQuit);
        
        mnImageOps = new JMenu("Image");
        mnImageOps.setMnemonic('i');
        menuBar.add(mnImageOps);
        
        mntmInstrumentalParameters = new JMenuItem("Instrumental Parameters");
        mntmInstrumentalParameters.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        mntmInstrumentalParameters.setMnemonic('i');
        mntmInstrumentalParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmInstrumentalParameters_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmInstrumentalParameters);
        
        mntmLabCalibration = new JMenuItem("LaB6 Calibration");
        mntmLabCalibration.setMnemonic('l');
        mntmLabCalibration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmLabCalibration_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmLabCalibration);
        
        mntmExcludedZones = new JMenuItem("Excluded Zones");
        mntmExcludedZones.setMnemonic('x');
        mntmExcludedZones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmExcludedZones_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmExcludedZones);
        
        mntmBackgroundSubtraction = new JMenuItem("Background Subtraction");
        mntmBackgroundSubtraction.setMnemonic('b');
        mntmBackgroundSubtraction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmBackgroundSubtraction_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmBackgroundSubtraction);
        
        mntmRadialIntegration = new JMenuItem("Radial Integration");
        mntmRadialIntegration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        mntmRadialIntegration.setMnemonic('r');
        mntmRadialIntegration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmRadialIntegration_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmRadialIntegration);
        
        mntmHpTools = new JMenuItem("HP Cu Pcalc.");
        mntmHpTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmHpTools_actionPerformed(e);
            }
        });
        mntmHpTools.setMnemonic('h');
        mnImageOps.add(mntmHpTools);
        
        mnGrainAnalysis = new JMenu("Grain Analysis");
        mnGrainAnalysis.setMnemonic('g');
        menuBar.add(mnGrainAnalysis);
        
        mntmDincoSol = new JMenuItem("Load tts-INCO SOL/PCS files");
        mntmDincoSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmDincoSol_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmDincoSol);
        
        mntmLoadXdsFile = new JMenuItem("Load XDS file");
        mntmLoadXdsFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmLoadXdsFile_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmLoadXdsFile);
        
        mntmFindPeaks = new JMenuItem("Find/Integrate Peaks");
        mntmFindPeaks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmFindPeaks_actionPerformed(arg0);
            }
        });
//        mntmFindPeaks.setEnabled(false);
        mnGrainAnalysis.add(mntmFindPeaks);
        
        mntmClearAll = new JMenuItem("Clear all");
        mntmClearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmClearAll_actionPerformed(e);
            }
        });
        
        mntmScDataTo = new JMenuItem("SC data to INCO");
        mntmScDataTo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmScDataTo_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmScDataTo);
        mnGrainAnalysis.add(mntmClearAll);
        
        mnPhaseId = new JMenu("Phase ID");
        mnPhaseId.setMnemonic('p');
        menuBar.add(mnPhaseId);
        
        mntmDatabase = new JMenuItem("Database");
        mntmDatabase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmDatabase_actionPerformed(e);
            }
        });
        mntmDatabase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        mntmDatabase.setMnemonic('d');
        mnPhaseId.add(mntmDatabase);
        
        mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);
        
        mntmAbout = new JMenuItem("About");
        mnHelp.add(mntmAbout);
        
        mntmManual = new JMenuItem("Manual");
        mntmManual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmManual_actionPerformed(e);
            }
        });
        mnHelp.add(mntmManual);
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmAbout_actionPerformed(e);
            }
        });
        this.contentPane = new JPanel();
        this.contentPane.setBorder(null);
        setContentPane(this.contentPane);
        contentPane.setLayout(new MigLayout("fill, insets 0", "[1200px,grow]", "[900px,grow]"));
        this.splitPane = new JSplitPane();
        this.splitPane.setBorder(null);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setResizeWeight(0.9);
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.contentPane.add(this.splitPane, "cell 0 0,grow,");
        this.panel_stat = new JPanel();
        this.splitPane.setRightComponent(this.panel_stat);
        this.panel_stat.setBackground(Color.BLACK);
        this.panel_stat.setBorder(null);
        panel_stat.setLayout(new MigLayout("fill, insets 5", "[1166px]", "[100px,grow]"));
        this.scrollPane = new JScrollPane();
        scrollPane.setViewportBorder(null);
        this.scrollPane.setBorder(null);
        this.panel_stat.add(this.scrollPane, "cell 0 0,grow");
        this.tAOut = new LogJTextArea();
        this.tAOut.setTabSize(4);
        this.tAOut.setWrapStyleWord(true);
        this.tAOut.setLineWrap(true);
        this.tAOut.setMaximumSize(new Dimension(32767, 32767));
        this.tAOut.setRows(1);
        this.tAOut.setBackground(Color.BLACK);
        this.scrollPane.setViewportView(this.tAOut);
        this.panel_all = new JPanel();
        this.splitPane.setLeftComponent(this.panel_all);
        panel_all.setLayout(new MigLayout("fill, insets 0", "[1200px,grow]", "[][grow]"));
        
        panel_2 = new JPanel();
        panel_all.add(panel_2, "cell 0 0,grow");
                panel_2.setLayout(new MigLayout("fill, insets 0", "[][grow][][]", "[grow]"));
        
                this.btnOpen = new JButton("Open Image");
                panel_2.add(btnOpen, "cell 0 0,alignx center,aligny center");
                this.btnOpen.setMargin(new Insets(2, 7, 2, 7));
                this.btnOpen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnOpen_actionPerformed(arg0);
                    }
                });
        this.lblOpened = new JLabel("(no image loaded)");
        lblOpened.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                do_lblOpened_mouseReleased(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                do_lblOpened_mouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                do_lblOpened_mouseExited(e);
            }
        });
        panel_2.add(lblOpened, "cell 1 0,alignx left,aligny center");
        
        btnPrev = new JButton("<");
        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnPrev_actionPerformed(e);
            }
        });
        panel_2.add(btnPrev, "cell 2 0,alignx center,aligny center");
        btnPrev.setToolTipText("Previous image (by index)");
        
        btnNext = new JButton(">");
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnNext_actionPerformed(arg0);
            }
        });
        panel_2.add(btnNext, "cell 3 0,alignx center,aligny center");
        btnNext.setToolTipText("Next image (by index)");
        this.splitPane_1 = new JSplitPane();
        this.splitPane_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.splitPane_1.setContinuousLayout(true);
        this.splitPane_1.setResizeWeight(1.0);
        this.panel_all.add(this.splitPane_1, "cell 0 1,grow");

        this.panelImatge = new ImagePanel(D2Dplot_global.sideControls);
        
        
        JScrollPane jspimage = new JScrollPane();
        jspimage.setViewportBorder(null);
        jspimage.setBorder(null);
        jspimage.setViewportView(this.panelImatge);
        this.splitPane_1.setLeftComponent(jspimage);
        
//        this.splitPane_1.setLeftComponent(this.panelImatge);
        this.panelImatge.setBorder(null);
        this.panel_controls = new JPanel();
        
        JScrollPane jscontrols = new JScrollPane();
        jscontrols.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jscontrols.setViewportBorder(null);
        jscontrols.setBorder(null);
        jscontrols.setViewportView(this.panel_controls);
        this.splitPane_1.setRightComponent(jscontrols);
        
//        this.splitPane_1.setRightComponent(this.panel_controls);
        panel_controls.setLayout(new MigLayout("fill, insets 0", "[130px,grow]", "[5px:20px:35px,grow][::160px,grow][::85px,grow][::140px,grow][::130px,grow][grow]"));
        
        btnInstParameters = new JButton("I. Parameters");
        btnInstParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnInstParameters_actionPerformed(arg0);
            }
        });
        panel_controls.add(btnInstParameters, "cell 0 0,growx,aligny center");
        this.panel_opcions = new JPanel();
        this.panel_controls.add(this.panel_opcions, "cell 0 1,grow");
        this.panel_opcions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.btnResetView = new JButton("Fit");
//        btnResetView.setPreferredSize(new Dimension(90, 32));
        this.btnResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnResetView_actionPerformed(arg0);
            }
        });
        panel_opcions.setLayout(new MigLayout("fill, insets 0", "[60px][60]", "[5px:20px:35px][5px:20px:35px][5px:20px:35px][5px:20px:35px]"));
        this.btnResetView.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnResetView, "cell 0 0,growx,aligny center");
        this.btnMidaReal = new JButton("100%");
//        btnMidaReal.setPreferredSize(new Dimension(90, 32));
        this.btnMidaReal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnMidaReal_actionPerformed(e);
            }
        });
        this.btnMidaReal.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnMidaReal, "cell 1 0,growx,aligny center");
        this.btn05x = new JButton("0.5x");
        panel_opcions.add(btn05x, "cell 0 1,growx,aligny center");
//        btn05x.setPreferredSize(new Dimension(50, 32));
        this.btn05x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btn05x_actionPerformed(arg0);
            }
        });
        this.btn05x.setMargin(new Insets(1, 2, 1, 2));
        this.btn2x = new JButton("2x");
        panel_opcions.add(btn2x, "cell 1 1,growx,aligny center");
//        btn2x.setPreferredSize(new Dimension(50, 32));
        this.btn2x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btn2x_actionPerformed(e);
            }
        });
        this.btn2x.setMargin(new Insets(1, 7, 1, 7));
        
        chckbxColor = new JCheckBox("Color");
        chckbxColor.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxColor_itemStateChanged(arg0);
            }
        });
        panel_opcions.add(chckbxColor, "cell 0 2");
        
        chckbxPaintExz = new JCheckBox("Paint ExZ");
        chckbxPaintExz.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxPaintExz_itemStateChanged(arg0);
            }
        });
        
        chckbxInvertY = new JCheckBox("flip Y");
        chckbxInvertY.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxInvertY_itemStateChanged(e);
            }
        });
        panel_opcions.add(chckbxInvertY, "cell 1 2");
        panel_opcions.add(chckbxPaintExz, "cell 0 3 2 1");
        
        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Points", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel, "cell 0 2,grow");
        panel.setLayout(new MigLayout("fill, insets 0", "[]", "[5px:20px][5px:20px,grow]"));
        this.chckbxIndex = new JCheckBox("Sel. Points");
        panel.add(chckbxIndex, "cell 0 0,growx,aligny center");
        chckbxIndex.setSelected(true);
        this.btnSaveDicvol = new JButton("Points List");
        panel.add(btnSaveDicvol, "cell 0 1,growx,aligny center");
        this.btnSaveDicvol.setMinimumSize(new Dimension(100, 28));
//        this.btnSaveDicvol.setPreferredSize(new Dimension(100, 32));
        this.btnSaveDicvol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveDicvol_actionPerformed(arg0);
            }
        });
        this.btnSaveDicvol.setMargin(new Insets(2, 2, 2, 2));
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Phase ID", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_controls.add(panel_3, "cell 0 3,grow");
        panel_3.setLayout(new MigLayout("fill, insets 0, hidemode 3", "[120px]", "[5px:20px][][5px:20px][5px:20px][]"));
        
        btnDbdialog = new JButton("Database");
//        btnDbdialog.setPreferredSize(new Dimension(100, 32));
        btnDbdialog.setMargin(new Insets(2, 2, 2, 2));
        panel_3.add(btnDbdialog, "cell 0 0,growx,aligny center");
        
        separator_1 = new JSeparator();
        panel_3.add(separator_1, "cell 0 1,growx,aligny center");
        
        chckbxShowRings = new JCheckBox("Quicklist");
        panel_3.add(chckbxShowRings, "cell 0 2,growx,aligny center");
        
        combo_LATdata = new JComboBox();
        combo_LATdata.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_combo_showRings_itemStateChanged(arg0);
            }
        });
        panel_3.add(combo_LATdata, "cell 0 3,growx,aligny center");
        combo_LATdata.setModel(new DefaultComboBoxModel(new String[] {}));
        
        btnAddLat = new JButton("Add to List");
        btnAddLat.setVisible(false);
        btnAddLat.setEnabled(false);
//        btnAddLat.setPreferredSize(new Dimension(100, 32));
        panel_3.add(btnAddLat, "cell 0 4,growx,aligny center");
        
        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Shortcuts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel_1, "cell 0 4,grow");
        panel_1.setLayout(new MigLayout("fill, insets 0", "[grow]", "[5px:20px][5px:20px][5px:20px]"));
        
        btnPeakSearchint = new JButton("PK search");
        btnPeakSearchint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnPeakSearchint_actionPerformed(arg0);
            }
        });
        panel_1.add(btnPeakSearchint, "cell 0 0,growx,aligny center");
        
        btnTtsdincoSol = new JButton("tts-INCO");
        btnTtsdincoSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnTtsdincoSol_actionPerformed(e);
            }
        });
        panel_1.add(btnTtsdincoSol, "flowy,cell 0 1,growx,aligny center");
        
        btnRadIntegr = new JButton("Rad. Integr");
        btnRadIntegr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRadIntegr_actionPerformed(e);
            }
        });
        panel_1.add(btnRadIntegr, "cell 0 2,growx,aligny center");
        
        chckbxShowRings.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxShowRings_itemStateChanged(arg0);
            }
        });
        btnDbdialog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnDbdialog_actionPerformed(arg0);
            }
        });
    }

    private void inicialitza() {
//        this.setSize(1200, 960); //ho centra el metode main
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        if(this.getWidth()>screenSize.width||this.getHeight()>screenSize.height){
//            this.setSize(screenSize.width-100,screenSize.height-100);
//        }
        
        //HO FEM CABRE (170322)
        this.setSize(MainFrame.getDef_Width(), MainFrame.getDef_Height()); //ho centra el metode main
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        while(this.getWidth()>screenSize.width){
            this.setSize(this.getWidth()-100, this.getHeight());
        }
        while(this.getHeight()>screenSize.height){
            this.setSize(this.getWidth(), this.getHeight()-100);
        }
        
        tAOut.stat(D2Dplot_global.welcomeMSG);
        FileUtils.setLocale();
        
        //inicialitzem l'arraylist a D2Dplot_global --- ja ho fa el metode en questio a global
        //BUSQUEM SI HI HA FITXERS .LAT al directori del programa i els afegim a quicklist (D2Dplot_global)
        if(D2Dplot_global.isConfigFileReaded()==null){
            tAOut.stat(String.format("No config file found on: %s, it will be created on exit!",D2Dplot_global.configFilePath));
        }else{
            if(D2Dplot_global.isConfigFileReaded()==true){
                tAOut.stat(String.format("Config file readed: %s",D2Dplot_global.configFilePath));    
            }else{
                tAOut.stat(String.format("Error reading config file: %s",D2Dplot_global.configFilePath));
            }
        }
        D2Dplot_global.initPars();
        D2Dplot_global.init_ApplyColorsToIPanel(this.getPanelImatge());
        D2Dplot_global.checkDBs();
        boolean ok = PDDatabase.populateQuickList(false);
        if (ok){
            tAOut.stat(String.format("QuickList DB file: %s",PDDatabase.getLocalQL()));
        }
        combo_LATdata.setPrototypeDisplayValue("XX"); 
        updateQuickList();
        
        
        //MIDES BUTONS
        btnInstParameters.setPreferredSize(new Dimension(110,30));
        btnInstParameters.setMinimumSize(new Dimension(110,15));
        btnResetView.setPreferredSize(new Dimension(50,30));
        btnResetView.setMinimumSize(new Dimension(50,15));
        btnMidaReal.setPreferredSize(new Dimension(50,30));
        btnMidaReal.setMinimumSize(new Dimension(50,15));
        btn05x.setPreferredSize(new Dimension(50,30));
        btn05x.setMinimumSize(new Dimension(50,15));
        btn2x.setPreferredSize(new Dimension(50,30));
        btn2x.setMinimumSize(new Dimension(50,15));
        btnSaveDicvol.setPreferredSize(new Dimension(110,30));
        btnSaveDicvol.setMinimumSize(new Dimension(110,15));
        btnDbdialog.setPreferredSize(new Dimension(110,30));
        btnDbdialog.setMinimumSize(new Dimension(110,15));
        btnPeakSearchint.setPreferredSize(new Dimension(110,30));
        btnPeakSearchint.setMinimumSize(new Dimension(110,15));
        btnTtsdincoSol.setPreferredSize(new Dimension(110,30));
        btnTtsdincoSol.setMinimumSize(new Dimension(110,15));
        btnRadIntegr.setPreferredSize(new Dimension(110,30));
        btnRadIntegr.setMinimumSize(new Dimension(110,15));
        
        
        SystemInfo si = new SystemInfo();
        log.info(si.MemInfo());
    }
    
    public static void updateQuickList(){
        Iterator<PDCompound> itrC= PDDatabase.getQuickListIterator();
        combo_LATdata.removeAllItems();
        while (itrC.hasNext()){
            combo_LATdata.addItem(itrC.next());
        }
    }
    
    //tanco tot
    private void reset() {
        this.fileOpened = false;
        this.panelImatge.setImagePatt2D(null);
        this.patt2D = null;
    }
    
    private void closePanels(){
        if (this.d2DsubWin != null) {
            this.d2DsubWin.dispose();
            this.d2DsubWin = null;
        }
        if (this.calibration != null) {
            this.calibration.inicia();;
        }
        if (this.exZones != null) {
            this.exZones.inicia();
        }
        if (this.dincoFrame != null) {
            this.dincoFrame.inicia();;
        }
        if (this.pksframe != null) {
            this.pksframe.inicia(false);
        }
        if (this.paramDialog != null) {
            this.updateIparameters();
        }
        if (this.dbDialog != null) {
            this.dbDialog.inicia();;
        }
        if (this.irWin != null) {
            this.irWin.inicia();;
            
        }
        if (this.pkListWin != null) {
            this.pkListWin.loadPeakList();
        }
        
        //TODO ADDD NEW THINGS IF NECESSARY
    }
    
    public void updatePatt2D(File d2File) {
        D2Dplot_global.setWorkdir(d2File);
        log.info("workdir="+getWorkdir());
        patt2D = ImgFileUtils.readPatternFile(d2File,true);
        
        if (patt2D != null) {
            panelImatge.setImagePatt2D(patt2D);
            panelImatge.setMainFrame(this);
            lblOpened.setText(d2File.toString());
            tAOut.stat("File opened: " + d2File.getName() + ", " + patt2D.getPixCount() + " pixels ("
                    + (int) patt2D.getMillis() + " ms)");
            if(patt2D.oldBIN)tAOut.ln("*** old BIN format detected and considered ***");
            tAOut.ln(patt2D.getInfo());
            fileOpened = true;
            openedFile = d2File;
            this.updateIparameters();
        } else {
            tAOut.stat("Error reading 2D file");
            tAOut.stat("No file opened");
            fileOpened = false;
            openedFile = null;
        }
    }
    
    public void updatePatt2D(Pattern2D p2D, boolean verbose) {
      this.patt2D = p2D;
      if (patt2D != null) {
          panelImatge.setImagePatt2D(patt2D);
          panelImatge.setMainFrame(this);
          String fname;
          if(patt2D.getImgfile()!=null){
              fname = patt2D.getImgfile().getName();
              tAOut.stat("File opened: " + fname + ", " + patt2D.getPixCount() + " pixels ("
                      + (int) patt2D.getMillis() + " ms)");
          }else{
              fname = "Data image not saved to a file";
              tAOut.stat("File opened: Data image not saved to a file");
          }
          lblOpened.setText(fname);
          if(patt2D.oldBIN)tAOut.ln("*** old BIN format detected and considered ***");
          if(verbose)tAOut.ln(patt2D.getInfo());
          fileOpened = true;
          openedFile = patt2D.getImgfile();
          this.updateIparameters();
      } else {
          tAOut.stat("Error reading 2D file");
          tAOut.stat("No file opened");
          fileOpened = false;
          openedFile = null;
      }
    }
    
    public void updateIparameters(){
        if (patt2D != null) {
            if (paramDialog!=null){
                paramDialog.inicia();
            }
        }
    }
    
    public PDCompound getQuickListCompound(){
        log.debug("getQuickListCompound CALLED");
        try{
            PDCompound pdc = (PDCompound) combo_LATdata.getSelectedItem();    
            return pdc;
        }catch(Exception e){
            log.debug("error in quicklist casting to PDCompound");
            return null;
        }
    }
    
    protected void do_this_windowClosing(WindowEvent e) {
      //FIRST SAVE OPTIONS
      D2Dplot_global.writeParFile();
      
      //SECOND SAVE QL FILE IF MODIFIED
      if(PDDatabase.isQLmodified()){
          //prompt and save QL file if necessary
          Object[] options = {"Yes","No"};
          int n = JOptionPane.showOptionDialog(null,
                  "QuickList has changed, overwrite current default file?",
                  "Update QL",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, //do not use a custom Icon
                  options, //the titles of buttons
                  options[1]); //default button title
          if (n==JOptionPane.YES_OPTION){
              PDDatabase.saveDBfileWorker saveDBFwk = new PDDatabase.saveDBfileWorker(new File(PDDatabase.getLocalQL()),true);
              saveDBFwk.execute();    
              int maxCount = 20; //maximum wait 10 seconds
              while (!saveDBFwk.isDone() || maxCount <=0){
                  try {
                    Thread.sleep(500);
                    maxCount = maxCount -1;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
              }
          } 
      }
      this.dispose();
    }
    
    protected void do_btn05x_actionPerformed(ActionEvent arg0) {
        if (panelImatge.getScalefit() > ImagePanel.getMinScaleFit()) {
            panelImatge.setScalefit(panelImatge.getScalefit() * 0.5f);
        }
    }

    protected void do_btn2x_actionPerformed(ActionEvent e) {
        if (panelImatge.getScalefit() < ImagePanel.getMaxScaleFit()) {
            panelImatge.setScalefit(panelImatge.getScalefit() * 2.0f);
        }
    }

    protected void do_mntmInstrumentalParameters_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (paramDialog==null){
                paramDialog = new Param_dialog(this.getPanelImatge(),patt2D);
            }
            paramDialog.setVisible(true);
        }
    }

    protected void do_btnInstParameters_actionPerformed(ActionEvent arg0) {
        mntmInstrumentalParameters.doClick();
    }
    
    protected void do_mntmLabCalibration_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            // tanquem zones excloses en cas que estigui obert
            if (exZones != null)
                exZones.dispose();
            if (pksframe != null)
                pksframe.dispose();
            if (calibration == null) {
                calibration = new Calib_dialog(this.getPanelImatge());
            }
            calibration.setVisible(true);
            panelImatge.setCalibration(calibration);
        }
    }
    protected void do_mntmExcludedZones_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            // tanquem calibracio en cas que estigui obert
            if (calibration != null)
                calibration.dispose();
            if (pksframe != null)
                pksframe.dispose();
            if (exZones == null) {
                exZones = new ExZones_dialog(this.getPanelImatge());
            }
            exZones.setVisible(true);
            this.chckbxPaintExz.setSelected(true);
            panelImatge.setExZones(exZones);
        }
    }
    
    protected void do_mntmFindPeaks_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            if (exZones != null)
                exZones.dispose();
            if (calibration != null)
                calibration.dispose();
            if (pksframe == null) {
                pksframe = new PKsearch_frame(this.panelImatge,false,null);
            }
//            pksframe.setPatt2D(patt2D);
            pksframe.setVisible(true);
            panelImatge.setPKsearch(pksframe);
        }

    }
    
    protected void do_mntmBackgroundSubtraction_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (d2DsubWin == null) {
                d2DsubWin = new D2Dsub_frame(patt2D,this);
            }
            d2DsubWin.setVisible(true);
        }else{
            //obrim el batch
            if (d2DsubWinBatch == null){
                d2DsubWinBatch = new D2Dsub_batch(this);
            }
            d2DsubWinBatch.setVisible(true);
        }
    }
    protected void do_mntmRadialIntegration_actionPerformed(ActionEvent e) {
        if(patt2D != null){
            if(this.irWin==null){
                irWin = new IntegracioRadial(this.getPanelImatge());
            }
            irWin.setVisible(true); 
        }
    }
    

    protected void do_btnTtsdincoSol_actionPerformed(ActionEvent e) {
        mntmDincoSol.doClick();
    }
    
    protected void do_btnRadIntegr_actionPerformed(ActionEvent e) {
//        testLP test = new testLP(patt2D);
//        debug_ellipse();
        mntmRadialIntegration.doClick();
    }

    protected void do_btnMidaReal_actionPerformed(ActionEvent e) {
        panelImatge.setScalefit(1.0f);
    }

    protected void do_btnOpen_actionPerformed(ActionEvent arg0) {
        this.openImgFile();
    }
    
    protected void do_mntmOpen_actionPerformed(ActionEvent e) {
        this.openImgFile();
    }

    protected void do_mntmSaveImage_actionPerformed(ActionEvent e) {
        this.saveImgFile();
    }

    protected void do_btnResetView_actionPerformed(ActionEvent arg0) {
        panelImatge.resetView();
    }
    
    // Obre finestra amb llista de pics
    protected void do_btnSaveDicvol_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            if (pkListWin != null) {
                pkListWin.tanca();
            }
            pkListWin = new Pklist_dialog(panelImatge);
            pkListWin.setVisible(true);
            pkListWin.toFront();
        }
    }

    protected void do_btnNext_actionPerformed(ActionEvent arg0) {
        
        String fnameCurrent = FileUtils.getFNameNoExt(patt2D.getImgfile());
        String fextCurrent = FileUtils.getExtension(patt2D.getImgfile());
        log.debug("fnameCurrent (no Ext): "+fnameCurrent);
        log.debug("fextCurrent: "+fextCurrent);
        
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        String fnameExtNew = "";
        try{
            log.debug("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            imgNum = imgNum+1;
            fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-4)+String.format("%04d", imgNum)+"."+fextCurrent;
        }catch(Exception e){
            log.debug("trying to get the file numbering");
            int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio>0){
                log.debug("index guio="+indexGuio);
                int imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio+1, fnameCurrent.length()));
                imgNum = imgNum+1;
                int lenformat = fnameCurrent.length()-indexGuio-1;
                log.debug("lenformat="+lenformat);
                String format = "%0"+lenformat+"d";
                log.debug("format="+format);
                fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-lenformat)+String.format(format, imgNum)+"."+fextCurrent;
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
            
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.closePanels();
            return;
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }

        //SECOND TEST buscar dXXX_0000.edf (realment podriem mirar la part esquerra del guio per ser mes generals...)
        
        //agafem el nom sense el _000X.edf
        String basFname = fnameCurrent.substring(0, fnameCurrent.length()-5);
        log.debug("basFname="+basFname);
        int indexNoDigit=-1;
        for (int i=1;i<basFname.length()-2;i++){
            char c = basFname.charAt(basFname.length()-i);
            if (!Character.isDigit(c)) {
                indexNoDigit=i;
                break;
            }
        }
        log.debug("indexNoDigit="+indexNoDigit);
        if (indexNoDigit>0){
            String sdom = basFname.substring(basFname.length()-(indexNoDigit-1), basFname.length());
            log.debug("sdom="+sdom);
            int ndom = -1;
            try{
                ndom = Integer.parseInt(sdom);
            }catch(Exception ex){
                log.debug("error parsing domain number");
            }
            if (ndom>=0){
                ndom = ndom + 1;
                fnameExtNew = basFname.substring(0,basFname.length()-(indexNoDigit-1)).concat(Integer.toString(ndom)).concat("_0000.").concat(fextCurrent);
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        
        d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.closePanels();
            return;
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
        
        if (fnameExtNew.isEmpty())return;
        
        //prova pksearchframe
        if (pksframe!=null){
            if (pksframe.getFileOut()!=null){
                pksframe.importOUT(pksframe.getFileOut());
            }
        }
        
    }
    protected void do_btnPrev_actionPerformed(ActionEvent e) {
        String fnameCurrent = FileUtils.getFNameNoExt(patt2D.getImgfile());
        String fextCurrent = FileUtils.getExtension(patt2D.getImgfile());
        log.debug("fnameCurrent (no Ext): "+fnameCurrent);
        log.debug("fextCurrent: "+fextCurrent);
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        
        String fnameExtNew = "";
        try{
            log.debug("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            imgNum = imgNum-1;
            fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-4)+String.format("%04d", imgNum)+"."+fextCurrent;
        }catch(Exception ex){
            log.debug("trying to get the file numbering");
            int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio>0){
                log.debug("index guio="+indexGuio);
                int imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio+1, fnameCurrent.length()));
                imgNum = imgNum-1;
                int lenformat = fnameCurrent.length()-indexGuio-1;
                log.debug("lenformat="+lenformat);
                String format = "%0"+lenformat+"d";
                log.debug("format="+format);
                fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-lenformat)+String.format(format, imgNum)+"."+fextCurrent;
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.closePanels();
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
        
        //agafem el nom sense el _000X.edf
        String basFname = fnameCurrent.substring(0, fnameCurrent.length()-5);
        log.debug("basFname="+basFname);
        int indexNoDigit=-1;
        for (int i=1;i<basFname.length()-2;i++){
            char c = basFname.charAt(basFname.length()-i);
            if (!Character.isDigit(c)) {
                indexNoDigit=i;
                break;
            }
        }
        log.debug("indexNoDigit="+indexNoDigit);
        if (indexNoDigit>0){
            String sdom = basFname.substring(basFname.length()-(indexNoDigit-1), basFname.length());
            log.debug("sdom="+sdom);
            int ndom = -1;
            try{
                ndom = Integer.parseInt(sdom);
            }catch(Exception ex){
                log.debug("error parsing domain number");
            }
            if (ndom>=0){
                ndom = ndom - 1;
                fnameExtNew = basFname.substring(0,basFname.length()-(indexNoDigit-1)).concat(Integer.toString(ndom)).concat("_0000.").concat(fextCurrent);
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        
        d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.closePanels();
            return;
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
        
        if (fnameExtNew.isEmpty())return;
        
        //prova pksearchframe
        if (pksframe!=null){
            if (pksframe.getFileOut()!=null){
                pksframe.importOUT(pksframe.getFileOut());
            }
        }
    }

    protected void do_mntmQuit_actionPerformed(ActionEvent arg0) {
        this.do_this_windowClosing(null);
    }
    
    protected void do_mntmDatabase_actionPerformed(ActionEvent e) {
        btnDbdialog.doClick();
    }
    
    protected void do_btnDbdialog_actionPerformed(ActionEvent arg0) {
        // tanquem calibracio en cas que estigui obert
        if (calibration != null) calibration.dispose();
        if (exZones != null) exZones.dispose();

        if (dbDialog == null) {
            dbDialog = new DB_dialog(this.getPanelImatge());
        }
        dbDialog.setVisible(true);
        panelImatge.setDBdialog(dbDialog);
    }

    protected void do_chckbxShowRings_itemStateChanged(ItemEvent arg0) {
        if (combo_LATdata.getItemCount()>0){
            log.debug("do_chckbxShowRings_itemStateChanged CALLED");
            panelImatge.setShowQuickListCompoundRings(chckbxShowRings.isSelected(), this.getQuickListCompound());
        }

    }
   
    protected void do_combo_showRings_itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
            panelImatge.setShowQuickListCompoundRings(chckbxShowRings.isSelected(), this.getQuickListCompound());
        }
    }
    
    protected void do_mntmExportAsPng_actionPerformed(ActionEvent e) {
        File imFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getWorkdir()));
        int selection = fileChooser.showSaveDialog(null);
        if (selection == JFileChooser.APPROVE_OPTION) {
            imFile = fileChooser.getSelectedFile();
            int w = panelImatge.getPanelImatge().getSize().width;
            int h = panelImatge.getPanelImatge().getSize().height;
            
            log.debug(String.format("(frame) w=%d h=%d", w,h));
            
            Rectangle r = panelImatge.calcSubimatgeDinsFrame();
            Rectangle2D.Float rfr = new Rectangle2D.Float(r.x,r.y,r.width,r.height);
            
//            panelImatge.calcSubimatgeDinsFrame().x;
//            panelImatge.calcSubimatgeDinsFrame().y;
//            panelImatge.calcSubimatgeDinsFrame().width;
//            panelImatge.calcSubimatgeDinsFrame().height;
            
//            Point2D.Float pUL = panelImatge.getFramePointFromPixel(new Point2D.Float((float)r.getX(),(float)r.getY()));
//            Point2D.Float pBR =panelImatge.getFramePointFromPixel(new Point2D.Float((float)(r.getX()+r.getWidth()),(float)(r.getY()+r.getHeight())));
            
            rfr = panelImatge.rectangleToFrameCoords(rfr);
            w = (int) rfr.getWidth();
            h = (int) rfr.getHeight();
            
            log.debug(String.format("(rect) w=%d h=%d", w,h));

            
            String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Current plot size (Width x Heigth) is "+Integer.toString(w)+" x "+Integer.toString(h)+"pixels\n"
                            + "Scale factor to apply=",
                    "Apply scale factor",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "1.0");
            
            float factor = 1.0f;
            if ((s != null) && (s.length() > 0)) {
                try{
                    factor=Float.parseFloat(s);
                }catch(Exception ex){
                    log.debug("error reading factor");
                }
                log.writeNameNumPairs("config", true, "factor", factor);
            }
            
            if (factor>0){
                double pageWidth = panelImatge.getPanelImatge().getSize().width*factor;
                double pageHeight = panelImatge.getPanelImatge().getSize().height*factor;
                double imageWidth = panelImatge.getPanelImatge().getSize().width;
                double imageHeight = panelImatge.getPanelImatge().getSize().height;

//                double pageWidth = w*factor;
//                double pageHeight = h*factor;
//                double imageWidth = w;
//                double imageHeight = h;
                
                double scaleFactor = ImgFileUtils.getScaleFactorToFit(
                        new Dimension((int) Math.round(imageWidth), (int) Math.round(imageHeight)),
                        new Dimension((int) Math.round(pageWidth), (int) Math.round(pageHeight)));

                int width = (int) Math.round(pageWidth);
                int height = (int) Math.round(pageHeight);

                int xnew = (int) (rfr.getX()*scaleFactor);
                int ynew = (int) (rfr.getY()*scaleFactor);
                int wnew = (int) (w*scaleFactor);
                int hnew = (int) (h*scaleFactor);
                
                BufferedImage img = new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.scale(scaleFactor, scaleFactor);
                panelImatge.getPanelImatge().paintComponent(g2d);
                g2d.dispose();

                imFile = ImgFileUtils.exportPNG(imFile, img.getSubimage(xnew, ynew, wnew, hnew));
                
            }else{ //salvem imatge base amb mida ideal
                
                imFile = ImgFileUtils.exportPNG(imFile, panelImatge.getSubimage());

            }
                        
            if (imFile == null) {
                tAOut.stat("Error saving PNG file");
                return;
            }
            
        } else {
            tAOut.stat("Error saving PNG file");
            return;
        }
        tAOut.stat("File PNG saved: " + imFile.toString());
        D2Dplot_global.setWorkdir(imFile);
    }
    
    protected void do_mntmAbout_actionPerformed(ActionEvent e) {
        if (p2dAbout == null) {
            p2dAbout = new About_dialog();
        }
        p2dAbout.setVisible(true);
    }
    
    protected void do_mntmManual_actionPerformed(ActionEvent e) {
        if (p2dAbout == null) {
            p2dAbout = new About_dialog();
        }
        p2dAbout.openManual();
    }
    
    protected void do_chckbxColor_itemStateChanged(ItemEvent arg0) {
        panelImatge.setColor(chckbxColor.isSelected());
        log.debug("do_chckbxColor_itemStateChanged called");
        panelImatge.pintaImatge();
    }
    protected void do_chckbxInvertY_itemStateChanged(ItemEvent e) {
        panelImatge.setInvertY(chckbxInvertY.isSelected());
        log.debug("do_chckbxInvertY_itemStateChanged called");
        panelImatge.pintaImatge();
    }
    
    protected void do_chckbxPaintExz_itemStateChanged(ItemEvent arg0) {
        panelImatge.setPaintExZ(chckbxPaintExz.isSelected());
        log.debug("do_chckbxPaintExz_itemStateChanged called");
        panelImatge.pintaImatge();
    }
    
    public void setViewExZ(boolean state){
        this.chckbxPaintExz.setSelected(state);
    }

    protected void do_lblOpened_mouseReleased(MouseEvent e) {
        if (lblOpened.getText().startsWith("(no")||lblOpened.getText().isEmpty())return;
        
        File f = new File(lblOpened.getText());
//        String fpath = f.getParent();
        String fpath = f.getAbsolutePath();
        boolean opened=false;
        try {
            if(Desktop.isDesktopSupported()){
                Desktop.getDesktop().open(new File(fpath));
                opened=true;
            }else{
                if(FileUtils.getOS().equalsIgnoreCase("win")){
                    new ProcessBuilder("explorer.exe","/select,",lblOpened.getText()).start();
                    opened=true;
                }
                if(FileUtils.getOS().equalsIgnoreCase("lin")){
                    //kde dolphin
                    try{
                        new ProcessBuilder("dolphin",lblOpened.getText()).start(); 
                        opened=true;
                    }catch(Exception ex){
                        if(D2Dplot_global.isDebug())ex.printStackTrace();
                    }
                    //gnome nautilus
                    try{
                        new ProcessBuilder("nautilus",lblOpened.getText()).start(); 
                        opened=true;
                    }catch(Exception ex){
                        if(D2Dplot_global.isDebug())ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            if(D2Dplot_global.isDebug())ex.printStackTrace();
        }
        if(!opened)tAOut.addtxt(true,true,"Unable to open folder");
    }

    protected void do_lblOpened_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblOpened.setForeground(Color.blue);
    }
    protected void do_lblOpened_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.lblOpened.setForeground(Color.black);
    }
    
    protected void do_mntmDincoSol_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer SOL pero de totes formes obrirem el dialeg
        if (dincoFrame == null) {
            dincoFrame = new Dinco_frame(this.getPanelImatge());
        }
        if (!dincoFrame.hasSolutionsLoaded()){
            dincoFrame.setSOLMode();
            dincoFrame.openSOL();
        }
        dincoFrame.setVisible(true);
        panelImatge.setDinco(dincoFrame);
    }

    protected void do_mntmLoadXdsFile_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer XDS pero de totes formes obrirem el dialeg
        if (dincoFrame == null) {
            dincoFrame = new Dinco_frame(this.getPanelImatge());
        }
        if (!dincoFrame.hasSolutionsLoaded()){
            dincoFrame.setXDSMode();
            dincoFrame.openXDS();
        }
        dincoFrame.setVisible(true);
        panelImatge.setDinco(dincoFrame);
    }
    
    protected void do_mntmClearAll_actionPerformed(ActionEvent e) {
        this.getPatt2D().clearSolutions();
        this.dincoFrame.dispose();
    }

    protected void do_mntmHpTools_actionPerformed(ActionEvent e) {
        if (hpframe == null) {
            hpframe = new HPtools_frame(this.getPanelImatge());
        }
        hpframe.setVisible(true);
    }
    
    protected void do_mntmSubtractImages_actionPerformed(ActionEvent e) {
        
        Subtract_dialog sdiag = new Subtract_dialog();
        sdiag.setVisible(true);
        
        Pattern2D img = ImgFileUtils.readPatternFile(sdiag.getImage(),true);
        float fac = sdiag.getFactor();
        Pattern2D img2 = ImgFileUtils.readPatternFile(sdiag.getBkgImage(),true);
        
        if(img==null)return;
        if(img2==null)return;
        
        Pattern2D dataSub = ImgOps.subtractBKG_v2(img, img2, fac, tAOut)[0];
        updatePatt2D(dataSub,false);    
    }
    

    
    protected void do_mntmSumImages_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(this,new File(getWorkdir()), filt, filt.length-1);
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);

        pm = new ProgressMonitor(null,
                "Summing Images...",
                "", 0, 100);
        pm.setProgress(0);
        sumwk = new ImgOps.sumImagesFileWorker(flist,tAOut);
        sumwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    log.fine("hi from inside if progress");
                    if (pm.isCanceled() || sumwk.isDone()) {
                        log.fine("hi from inside if cancel/done");
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            sumwk.cancel(true);
                            log.debug("sumwk canceled");
                        } else {
                            log.debug("sumwk finished!!");
                        }
                        pm.close();
                    }
                }
                if (sumwk.isDone()){
                    log.fine("hi from outside if progress");
                    Pattern2D suma = sumwk.getpattSum();
                    if (suma==null){
                        tAOut.stat("Error summing files");
                        return;
                    }else{
                        updatePatt2D(suma,false);    
                    }
                }
            }
        });
        sumwk.execute();
    }
    

    
    protected void do_mntmBatchConvert_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(this,new File(getWorkdir()), filt, filt.length-1);
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);
        
        pm = new ProgressMonitor(null,
                "Converting Images...",
                "", 0, 100);
        pm.setProgress(0);
        convwk = new ImgFileUtils.batchConvertFileWorker(flist,tAOut);
        convwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    if (pm.isCanceled() || convwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            convwk.cancel(true);
                            log.debug("Batch convert stopped by user");
                        } else {
                            log.debug("Batch convert finished!!");
                        }
                        pm.close();
                    }
                }
            }
        });
        convwk.execute();
    }
    
    private void openImgFile(){
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File d2File = FileUtils.fchooser(this,new File(getWorkdir()), filt, filt.length-1, false, false);
        if (d2File == null){
            if (!fileOpened){
                tAOut.stat("No data file selected");
            }
            return;
        }
        D2Dplot_global.setWorkdir(d2File);
        
        // resetejem
        this.reset();
        this.updatePatt2D(d2File);
        this.closePanels();
    }
    
    private void saveImgFile(){
        if (this.getPatt2D()!=null){
            FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterWrite();
            File f = FileUtils.fchooser(this,new File(getWorkdir()), filt, filt.length-1, true, true);
            if (f!=null){
                D2Dplot_global.setWorkdir(f);
                File outf = ImgFileUtils.writePatternFile(f,this.getPatt2D());
                if (outf!=null){
                    int n = JOptionPane.showConfirmDialog(this, "Load the new saved file?", "Refresh file", JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        this.reset();
                        this.updatePatt2D(outf);
                        this.closePanels();
                    }
                }else{
                    tAOut.stat("Error saving file");
                }
            }
        }else{
            tAOut.stat("Choose an image file saving");
        }
    }
    
    public File getOpenedFile() {
        return openedFile;
    }

    public ImagePanel getPanelImatge() {
        return panelImatge;
    }

    public Pattern2D getPatt2D() {
        return this.patt2D;
    }
    
    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }
    
    public LogJTextArea gettAOut() {
        return tAOut;
    }
    
    public boolean isSelectPoints(){
        return this.chckbxIndex.isSelected();
    }
    
    protected void do_btnPeakSearchint_actionPerformed(ActionEvent arg0) {
        mntmFindPeaks.doClick();
    }
    
    public Dinco_frame getDincoFrame() {
        return dincoFrame;
    }
    public void setDincoFrame(Dinco_frame dincoFrame) {
        this.dincoFrame = dincoFrame;
    }
    public Calib_dialog getCalibration() {
        return calibration;
    }
    public void setCalibration(Calib_dialog calibration) {
        this.calibration = calibration;
    }
    public IntegracioRadial getIrWin() {
        return irWin;
    }
    public void setIrWin(IntegracioRadial irWin) {
        this.irWin = irWin;
    }
    /**
     * @return the def_Width
     */
    public static int getDef_Width() {
        return def_Width;
    }
    /**
     * @param def_Width the def_Width to set
     */
    public static void setDef_Width(int def_Width) {
        MainFrame.def_Width = def_Width;
    }
    /**
     * @return the def_Height
     */
    public static int getDef_Height() {
        return def_Height;
    }
    /**
     * @param def_Height the def_Height to set
     */
    public static void setDef_Height(int def_Height) {
        MainFrame.def_Height = def_Height;
    }
    protected void do_mntmFastopen_actionPerformed(ActionEvent e) {
        VideoImg viframe = new VideoImg();
        viframe.setVisible(true);
    }
    
    protected void do_mntmScDataTo_actionPerformed(ActionEvent e) {
        SC_to_INCO_dialog scToIncoframe = new SC_to_INCO_dialog(this);
        scToIncoframe.setVisible(true);
    }
}
