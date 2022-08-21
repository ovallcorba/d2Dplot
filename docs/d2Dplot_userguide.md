# d2Dplot User's Guide
<div style="text-align: center;">

![](.//Pictures/100000010000014B0000014B950311360B3DCD36.png)   
(for version 2208)

Author: **Oriol Vallcorba**   
ALBA Synchrotron Light Source - CELLS (www.cells.es)

Collaborator: **Jordi Rius**    
Institut de Ciència de Materials de Barcelona (ICMAB)    
Consejo Superior de Investigaciones Científicas (CSIC) 

For comments/complaints/errors/suggestions, please contact to: **ovallcorba@cells.es**

</div>

***
- [d2Dplot User's Guide](#d2dplot-users-guide)
  - [1. Installation and use of *d2Dplot*](#1-installation-and-use-of-d2dplot)
    - [Configuration file](#configuration-file)
  - [2. Overview](#2-overview)
  - [3. Image menu modules](#3-image-menu-modules)
    - [Instrumental parameters](#instrumental-parameters)
    - [Instrumental Parameters Calibration](#instrumental-parameters-calibration)
    - [Excluded zones](#excluded-zones)
    - [Background subtraction](#background-subtraction)
    - [Integration of 2DXRD to 1DXRD](#integration-of-2dxrd-to-1dxrd)
    - [Azimuthal (circular) plot](#azimuthal-circular-plot)
    - [Copper pressure calculator (for High Pressure experiments)](#copper-pressure-calculator-for-high-pressure-experiments)
  - [4. Grain Analysis module](#4-grain-analysis-module)
    - [Find/Integrate Peaks](#findintegrate-peaks)
    - [Run TTS_software](#run-tts_software)
    - [Load tts-INCO SOL/PCS files](#load-tts-inco-solpcs-files)
    - [Load XDS file](#load-xds-file)
    - [SC data to INCO](#sc-data-to-inco)
  - [5. Phase ID](#5-phase-id)
    - [Database](#database)
  - [6. *Macro* mode](#6-macro-mode)
  - [7. Image formats info](#7-image-formats-info)
    - [D2D format](#d2d-format)
    - [BIN format](#bin-format)
    - [EDF format](#edf-format)
    - [IMG format](#img-format)
    - [GFRM format](#gfrm-format)
    - [SPR format](#spr-format)
    - [TIFF format](#tiff-format)
    - [CBF format](#cbf-format)
  - [8. Other file formats info](#8-other-file-formats-info)
    - [Database (DB) format](#database-db-format)
    - [Excluded zone (EXZ) format](#excluded-zone-exz-format)
  - [9. References](#9-references)
  - [10. Miscellaneous](#10-miscellaneous)
    - [Release notes](#release-notes)
    - [Contact information](#contact-information)
    - [Conditions of use](#conditions-of-use)
    - [Disclaimer](#disclaimer)
    - [Acknowledgments](#acknowledgments)
***


## 1. Installation and use of *d2Dplot* 

No installation of the program is required. Only extract the files and folders of the zip file into the desired folder in your hard drive and run the executable file (`d2Dplot.exe` in Windows and `d2Dplot` in Linux). In most of the recent Linux distributions, the executable files can be executed by double click from the file explorer but alternatively you can also run it from the command line with `./d2Dplot`. If the execute flag of the file is turned off, turn it on with: `chmod +x d2Dplot`

**Tip**: Running it from the command line has the advantage that you can give an image file as the argument and it will be automatically opened. Also you can use the *macro* mode to give instructions to operate the program through command line arguments (no GUI).

*Note:* A Java Runtime Environment is required (Java Platform, SE version 8 or higher).

### Configuration file

The first run, the program generates a plain text configuration file (`d2dconfig.cfg`) at the same folder where the program is installed. However, in some systems it can be created inside the user folder or somewhere else (the program will display the location of the file on the output panel located at the bottom part of the main window). Usually there is no need to change anything of this file but, if desired, the parameters are self-explanatory and their value can be modified.

The most important parameters are the default paths to the compound databases (*defQuickListDB* and *defCompoundDB*) which can be modified according to our preferences. 

## 2. Overview

This is the aspect of the main window after opening an image (via menu *File-Open*) or clicking the button *Open Image*.

![](.//Pictures/10000000000003F800000308B12E931F6F5ACBFE.png)

The main parts are:

1. **Menu bar**. To access all the program modules and options. It contains:

     - **File**
       - *Open Image*. Opens an image file.
       - *Save Image*. Save the image file (to any of the supported formats)
       - *Export as PNG.* Save as a PNG file.
       - *Sum Images*. To merge several images to a single one.
       - *Subtract Images*. To subtract one image from another.
       - *Batch Convert*. To convert a list of files to another file format.
       - *Fast Viewer*. To open a series of images to be displayed sequentially.
       - *Reset*. **Resets** the program**
       - *Quit*. Exit the program

     - ***Image***
    
       - *Instrumental Parameters.* To introduce the instrumental parameters of the image.
       - *Instr. Param. Calibration*. Use of a standard substance diffraction data (LaB<sub>6</sub>, Silicon, etc...) to calibrate the sample-to-detector distance, the beam center and the tilt/rot of the detector.
       - *Excluded Zones*. To select zones of the image that have to be omitted in further calculations.
       - *Background Subtraction.* To subtract the background of the image. In the case there is some contribution of a holder (glass, etc...) and we want to get a background clean image.
       - *Conversion to 1D PXRD.* To get the corresponding 1D (powder) pattern of the diffraction image.
       - *Azhimuthal (circular) plot.* To get the 1D plot of the intensity along a Debye ring.
       - *HP Cu Pcalc*. To calculate the system pressure from two peaks of the Cu used as internal standard in high pressure experiments.

     - **Grain Analysis**
       - *Find/Integrate Peaks*. Locate diffraction peaks on the image.
       - *Run tts\_Software*. Opens the front-end to launch the *TTS_software*.
       - *Load tts-INCO SOL/PCS files.* Open the files generated by tts-INCO or tts-REDUC programs to check the correctness of single grain orientations (Rius *et. al.* 2015, 2016).
       - *Load XDS file.* Open a spot.xds file from XDS (**X**-ray **D**etector **S**oftware, CCP4; Kabsch, 1988) to show the position of the peaks.
       - *SC data to INCO.* Convert single-crystal dataset (small angular step) to a wider step angle format for tts-INCO.

     - **Phase ID**
       - *Database*. Opens the compound database window. To plot theoretical rings from a compound database and search compounds from the image ring positions (more explained in the corresponding section of the guide).

     - **Help**
       - *About.* Some information about the program.
       - *Manual.* Link to this user's guide.**
       - *Check for updates.* To see if a new version of *d2Dplot* is available.

2. **Top bar**. It contains a button to quickly open an image file and also shows the path of the current displayed image. On the right part there are to arrow buttons which allow a quick navigation between consecutive images. Consecutive images are those which have the same filename followed by four sequential digits (e.g. `lab6_0000.d2d`, `lab6_0001.d2d`, `lab6_0002.d2d`, `lab6_0003.d2d`,...).

3. **Image panel**. Where the image is shown. The general interaction is:
    - Left mouse button: Selection, peak addition, etc... (depend on the opened module)
    - Middle mouse button: Press and drag to move the image. Click with no movement to fit the image to the display area.
    - Mouse wheel: Zoom.
    - Right mouse button: Deletion. Pres and drag (UP and DOWN) for zoom.

4. **Image panel controls**. Information about the current pixel we are pointing is shown here. Also the contrast can be adjusted with the slide. The *auto* checkbox is to calculate automatically the contrast value for every opened image (it is done by default on the first one opened but if a consecutive images are opened it is often desired to have it disabled for comparison).

5. **Right panel**. Here, we have:
    - Shortcuts for the instrumental parameters, radial integration, grain analysis, peak search/integrate and compound database.
    - Plotting options regarding the image display (*100%* means that a pixel of the screen corresponds to a pixel of the image). ExZ = Excluded zones.
    - Point selection tool. To select (by left mouse button clicking) points or rings of the image. The point list can be retrieved with the button Point List (and exported to a file if it wants to be used somewhere else). These points can be used later to search in the database (or calculate the pressure with Cu).
    - Quicklist. It contains a selection of the compounds for quick display of the rings. More about the quicklist is explained in the corresponding section of the guide.

6. **Output panel**. Some messages of the program are displayed here.

## 3. Image menu modules

### Instrumental parameters

<div style="text-align: center;">
<img src=".//Pictures/1000000000000164000002273AAF655868D1BAF5.png" width="280"/>
</div>

Instrumental and acquisition parameters are introduced here, names are self explanatory and the units are shown The Tilt/Rot convention used is:
  - Tilt: Deviation (angle) of the orthogonality of the beam direction.
  - Rot: Clockwise rotation (angle) of a perpendicular axis taking as “zero” the vertical (i.e. 12h on a clock).

<div style="text-align: center;">
<img src=".//Pictures/10000001000001290000019E30976E865B36A039.png" height="280"/>
<img src=".//Pictures/100000010000017900000154E7609E0C98910549.png" height="240"/>
</div>

The convention used is compatible with calibrations with *Fit2D* program (Hammersley, Svensson & Thompson, 1994). 

In the “?” dialog, a simulated LaB<sub>6</sub> diffraction image can be generated entering custom calibration values. 

If *keep calibration info for the session* is selected, no calibration info will be read from the header of next opened images. 

### Instrumental Parameters Calibration

<div style="text-align: center;">
<img src=".//Pictures/10000000000001440000025BA11E923CF4C60C67.png" width="240"/>
</div>

This module is for calibration of the sample-to-detector distance, the beam center and the detector tilt/rot angles from a calibrant substance (e.g. LaB<sub>6</sub>). The calibrant substance can be selected in the same window and there are two by default in *d2Dplot*, LaB<sub>6</sub> and Silicon. However, more calibrants can be added to the list by introducing additional lines in the config file (`d2dconfig.cfg`) starting with `calibrant = ` followed by an identifying name and a list of *d*-spacings separated by semicolons (;), for example:

`calibrant = LaB6 NIST-660B; 4.1568785; 2.9393575; 2.3999755; 2.0784323; 1.8590043`

To perform the calibration there are two options:

a) Click on *Autocalibration*. It will use the image header info (distance, wavelength, center, ...) as initial guess values to find the calibrant rings automatically. Try this method first.

b) Select manually the points on the first ring of the calibrant:

1. Click on *manual calibration* button
2. Click 5 points on the inner calibrant ring (do not need to be very accurate...)
![](.//Pictures/10000000000002FC000001B5D8EC791E904F615D.png)
3. Click on the same button (now labeled *Click here when finished*)
4. The rings and instrumental parameter values will be calculated
    ![](.//Pictures/10000000000003640000025B8906CBBCF308A674.png)
5. It can be repeated by clicking on the *Recalc* button (changing the parameters from the search rings if necessary). 

The display settings show more info regarding the search of the calibrant rings.

Buttons below allow to apply the calibration results to the current image or write a CAL file to be used as calibration info for batch processing of images (and/or when the header info is not enough or correct).

The instrumental parameters estimation (beam center, distance, detector tilt and rot) are obtained following the methodology described by Hart *et al.*, 2013

### Excluded zones

<div style="text-align: center;">

![](.//Pictures/10000000000003FA00000276682A741478A4233E.png)

</div>

To select zones of the image to discard in further calculations, you can:

  - Define a threshold such as if Y < Threshold the pixel will be excluded
  - Define a margin for the image (pixels on the borders to be excluded)
  - Define a detector radius in case the detection area is circular. 
  - Add beamstop shaped excluded zone by giving a radius of the central part of the beamstop, a pixel inside the arm of the beamstop and the width of the arm.
  - Add a polygonal excluded zone click ADD and click several points to define the zone.
  - Add an arc-shaped excluded zone by clicking 3 points to define the zone in the following order: center, half radial width, half azimuthal aperture. 
  - Paint with the mouse the zones you want to exclude by clicking *Mouse Free Paint* and left-click and drag with the mouse. You can select the size of the square-shaped “brush” and undo the last change with the button if desired (or clear all).

After defining excluded zones, you may do one of the following:

  - Save an Excluded Zones (ExZ) file to be loaded later and/or to apply the zones to other images.
  - Save a MASK image, which is an image in BIN format with all the intensities at zero except for the mask pixels which have intensity -1.
  - Save in a format (D2D, BIN) that contain the information.

On the main program window there is an option to show/hide the excluded
zones, which are painted in magenta (paint ExZ) if activated.

### Background subtraction

![](.//Pictures/10000000000003300000017F6B14B600A8827375.png)

There are 3 sections:

1. In the first one you can subtract a “glass” (or background) file by selecting it. A factor can be given (otherwise will be calculated in a conservative way and you can adjust it in next runs)

2. In the second one there are 5 methods to estimate the background, in summary:
      - *avsq*: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (*Npix*) and the number of iterations (*Niter*). It is a slow process for high *Npix* and *Niter* values.
      - *avarc*: The same as previous option but using arc shaped areas (within 2-theta) around each pixel. Set the number of iterations (*Niter*) and the factors for the width (*wdt*) and angular aperture (*ang*) for the arcs. This is a very slow method.
      - *avcirc*: The background estimation for each pixel is the mean intensity from a radial integration (in the 2-theta circle containing each pixel). Set the stepsize for the 2-theta ranges (*step*).
      - *minsq*: The background intensity value for each pixel (v0) is calculated as: Minimum (v0, v1, v2, v3) where v1, v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set which operations to use (*v,h,vh*), and the number of pixels (*Npix*) defining the square zone to be averaged after the operation (use 0 to consider only 1 pixel). It is a fast method but some peak intensity may be subtracted.
      - *minarc*: The same as *minsq* but using an arc shaped zone for each pixel. Set the operations (*v,h,vh*) and the factors for width and angular aperture (*wdt,ang*).

3. The third one is to apply the corrections for Lorentz & polarization
    to the image pixels. Select the proper conditions (single grain
    oscillating/powder, synchrotron/lab, oscillating axis
    horizontal/vertical).

When clicking on *Run* the sections marked with the *do it* tick will be executed.

Visual inspection for residual peak intensity in the subtracted background can be done by clicking the \[view BKG\] button. Result images can be seen on the main window and source image can be reloaded if wanted. It is recommended to save the result to an image file before applying more corrections to the result file.

To subtract the background it is very important to define the excluded zones before.

### Integration of 2DXRD to 1DXRD

It performs the conversion of the 2D diffraction image to the 1D powder diffraction pattern given a 2-theta range and conditions (fields are self-explanatory). Result can be saved in a two columns file (2-theta intensity). Considerations:

  - The cake ini/end units are degrees and starting from the vertical (12h. on a clock) going positive clockwise.
  - Azim bins (usually will be 1 for the full integration) is to divide the full integration (caki ini/end) in different cake fragments, so one pattern is generated for each part (check image above). It may be used to check for homogeneity or texture.
  - Add I is to add intensity to the pixels of the image. It is useful in case the detector by default adds intensity to avoid zero. To subtract this extra intensity we need to introduce a negative value here.

![](.//Pictures/10000000000005190000020BFAB45E2AD8800BE1.png)

Information for the radial integration methodology and geometrical corrections can be found on Hinrichsen, Dinnebier & Jansen, 2008; as the methodology implemented in d2Dplot follows the definitions on the book.

### Azimuthal (circular) plot

It performs a plot along the Debye ring specified by a 2-theta value with a tolerance (2-theta window) and an angular step (azim step). It is useful to check for graininess and for texture in powder samples.

![](.//Pictures/1000000000000493000002180EDE6370B7D4F5DD.png)

### Copper pressure calculator (for High Pressure experiments)

![](.//Pictures/100000000000021B000001B1A4E06B29866C2861.png)

This module is still very preliminary and the only function it has implemented is the calculation of the pressure by the selection of two copper peaks (or alternatively directly from a given cell parameter).

## 4. Grain Analysis module

This module contains tools to work with images of single/few grains, but not powders.

### Find/Integrate Peaks

![](.//Pictures/10000000000004E900000247A14664349F468A47.png)

This is an important module of *d2Dplot* for the *TTS\_software* interaction. It finds and integrates spots on the image. The options for the peak detection are:

  - ESD factor: It acts as a threshold related to the intensities standard deviation and optionally it can estimate the background for a better detection (it is slower).
  - Peak merge zone: to avoid very close peaks. Avg. position means that when the merging is done it is taking the mass center of the  peaks as the final peak position.
  - Minimum number of pixels for a peak.
  - By checking add/remove peaks, additional peaks can be added or removed by clicking with the left or right mouse button respectively. They are automatically integrated.
  - Remove Diamonds tries to detect and remove the peaks coming from diamonds in case of Diamond Anvil Cells. 
  - Remove Saturated removes the saturated spots from the list.

The integration options are:

  - Radial width of the integration zone (in pixels). Can be set to auto.
  - Azimuthal aperture of the integration zone (in degrees). Can be set to auto.
  - Number of pixels to calculate the background. Can be set to auto.
  - Lorentz correction according to the oscillation axis.

The results are shown on a table with a lot of information: 

  - XPix, YPix = Pixel coordinates.
  - Radius = Center to pixel vector modulus.
  - Ymax = Maximum intensity.
  - Fh2, s(FH2) = Integrated intensity and the associated standard deviation.
  - Ymean = Mean intensity.
  - Npix = Number of contributing pixels
  - Ybkg, sYbkg = Background intensity that has been subtracted and its standard deviation.
  - Nbkg = Number of background pixels used for the background estimation.
  - RadWth = Radial width in pixels of the integration area.
  - AzimDeg = Azimuthal aperture in degrees of the integration area.
  - dsp = d-spacing of the spot.
  - p = π·(ymax/yint)<sup>2/3</sup>
  - Swarm = If the peak has more than one maximum of intensity (may be overlap?)
  - Satur = If the peak contain saturated pixels (indicates the number).
  - nearMsk = True if the peak is close to a mask zone.

By clicking on a peak of the list the integration zone is shown on the image.

The peak list and intensity info can be exported as:

  - A text file containing all the information of the table (it can be imported back).
  - PCS file to be used in *tts-INCO* program (Rius *et. al.* 2015, 2016).

With the batch button several images can be processed using the same peak detection and integration parameters. The batch PCS generates an individual PCS file for each of the processed images. The batch OUT generates only one file containing the information of all the processed images. If the image on the main frame is changed (for example with the arrows on the top to navigate images), the peak search module is not closed and can be used to integrate directly the new image.

### Run TTS_software

![](.//Pictures/10000001000002EF0000026C49F38FAF215F01B4.png)

This option opens the front-end to the *TTS\_software.* This software is not included in *d2Dplot *and it should be downloaded separately from the website [http://departments.icmab.es/crystallography/software](http://departments.icmab.es/crystallography/software). The first time we need to tell *d2Dplot* where the *TTS_software* folder is located. It is done from the small configuration window that can be opened from the wrench tool button on the top right. The *TTS_software* comes with its own manual but in this section you can find a small step-to-step tutorial for its application using the front-end.

1. Preparation of the Data
    
    In order to apply the *TTS_software* to extract and merge the intensities from one or several diffracting grains of a sample, first we need the following files in the working folder:
      - Diffraction images with the proper nomenclature, which means a base name with a sequential numbering. In our case they will be: `dickin_14_0000.edf`, `dickin_14_0001.edf`, ... `dickin_14_0010.edf`. In total 11 images collected rotating *ϕ* from -25 to +25º using Δ*ϕ*= 5º.
      - The PCS file for each one of the images, generated in the find/integrate peaks module explained in the previous section.
      - A `MASK.BIN` file with the excluded zones (e.g. beam stop), generated in the Excluded Zones module explained in a previous section of this document.
      - We open the central frame in *d2Dplot*, in our case `dickin_14_0005.edf`

    ![](.//Pictures/100000010000037400000368441BEB88C5301900.png)

2. Generate the input file
      - In the *TTS_software* frontend we click on *Generate template TSD *and we create the file `dickin_14.tsd` and start editing it after creation (the program will ask us).
      - In the TSD file we must introduce fields CELL, LATTICE and LAUE according to our crystal. Then we set the SWING (which is Δ*ϕ*), DSFOU (min *d*-spacing to be considered) and the option IOFF=0 to indicate that we are performing a full scan of the central frame. MULTDOM, ALON, ALAT, ASPIN are left at zero at this stage as they are not used. For MODEL we also put zero (as none is used) and for the PCS we put 1 file which is the number 5 with the offset 0º. NSOL is the number of solutions that the program will output. At the end it should look like this:

        ```
        DICKINSONITE C2/c
        CELL
         16.625 10.0746 24.8365 90.0 105.24 90.0
        LATTICE
        C
        LAUE
        2
        \&CONTROL 
        SWING=5.0,
        DSFOU=1.0,
        MULTDOM=0,
        IOFF=0,
        NSOL=10,
        ALON=0.0,
        ALAT=0.0,
        SPIN=0.0
        /
        MODEL
         0
        PCS/HKL
        1
        5, 0.0 
        ```
   - We save and close the TSD file and click *RUN* on the tts_inco section. The output will show something like this:

      ![](.//Pictures/10000001000003FE00000451656258D9C05ADF7E.png)

3. Checking the results
    
    Now we can click on *check SOL* to see on the diffraction image the matching of the orientations found with the diffraction peaks. We can select the different solutions on the list to see the reflections on the image. In this case we can see that there are two different solutions for orientations that match the spots on the image (with 169 and 128 spots perfectly matching respectively).
    
    ![](.//Pictures/10000000000003DD00000242E23993D86BCB960D.png)

4. Extraction of partial hkl datasets from one oriented domain
    
    Now we have two domains oriented. We can select the first one (solution 1) and click on the *Create TSD IREF=1* button. When the program asks for a file name we put the same as before but adding “d1” to identify that it corresponds to the domain number 1 (so it will be `dickin_14d1.tsd`). We can check the contents of the file but it is not necessary to edit anything as the program already populated the list of images with the corresponding offsets, the IOFF=1 (to indicate that now we are exploring a complete dataset with one orientation set) and the ALON, ALAT and SPIN angles according to the orientation selected as solution.

    ```
    CELL
     16.625 10.0746 24.8365 90.0 105.24 90.0
    LATTICE
    C
    LAUE
    2
    \&CONTROL 
    SWING=5.,
    DSFOU=1.0,
    MULTDOM=0,
    IOFF=1,
    NSOL=10,
    ALON=186.667,
    ALAT=86.333,
    SPIN=246.333,
    /
    MODEL
     0
    PCS/HKL
    11
    0,-25.00
    1,-20.00
    2,-15.00
    3,-10.00
    4,-5.00
    5,0.00
    6,5.00
    7,10.00
    8,15.00
    9,20.00
    ```

5. Merge the partial dataset of one domain
    
    To merge the datasets of one domain the input file is the same TSD used in the previous step, so we click directly the *RUN button* in the *tts_Merge* section. The output will be similar to this one:

    ![](.//Pictures/10000000000003B400000171E6CA2ED676ED6ACC.png)

    where we can see the scale factors applied to each of the partial datasets and the residual of the merge process. Checking the residual and the evolution of the scale factor is a good indication of the consistency of the data. This information and a summary of the process can also be checked in the *MRG file*.

    The result is an HKL file (`dickin_14d1.HKL`), containing the extracted intensities for this domain.

6. Extract the intensities for all the different domains
    
    Steps 4 and 5 should be repeated for the different domains contained on the same set of images. Also, it may be the case that we have other sets of images containing different oriented grains of the same compound so we should repeat the process for them in order to get all the HKLs for the different individual domains.
    
    In this example we repeat steps 4 and 5 using the solution number 2, which is the orientation found for the second domain on our set of images.

7. Multidomain merging
    
    Finally we will perform the merging of datasets from multiple oriented domains. In this example we have datasets (HKL files) from two domains: `dickin_14d1.HKL` and `dickin_14d2.HKL`. To perform the multidomain merging we have to click on *Create TSD MULTDOM=1*. Here the program will ask to save a new TSD file, which in this case the name has no restrictions. Then the program will ask to select the HKL files we want to merge and we have to select the two corresponding to the full oriented datasets:

    ![](.//Pictures/100000000000024500000265C0648273015CA86C.png)
    
    We do not need to change anything from the new TSD file created, it will have MULTDOM=1 and the HKL files:

    ```
    DICKINSONITE C2/c
    CELL
     16.625 10.0746 24.8365 90.0 105.24 90.0
    LATTICE
    C
    LAUE
    2
    \&CONTROL 
    SWING=5.,
    DSFOU=1.0,
    MULTDOM=1,
    (...)
    PCS/HKL
    2
    dickin\_14d1.HKL
    dickin\_14d2.HKL
    ```

    We *RUN tts_Merge* again with this new TSD file and the output will be imilar as the merging of partial datasets (with the merge residual and scale factors).

    ![](.//Pictures/10000000000002930000017025FA4DC81541E571.png)

    At the working folder, the final HKL file (with the same name as this last TSD file) containing the intensity information of all the merged data will be generated in the SHELX HKLF 4 format (Sheldrick, 2014), to be used for structure refinement.

    As an example of the complexity of the refinements that can be performed, the SHELX output file .RES in space group C2/c of the dickinsonite phosphate from Cuevas (Argentina), derived from 3 domains with 11 images each one, is given below. The tts-diffraction data were measured at ALBA Synchrotron (Barcelona, Spain) in collaboration with Dr. Fernando Colombo (CICTERRA-CONICET, Córdoba, Argentina) in the frame of Project MAT2015-67593-P of MINECO & FEDER. The formula for this dickinsonite sample is K<sub>0.5</sub>Li<sub>0.2</sub>Na<sub>5.4</sub>Ca<sub>0.9</sub> Mn<sub>9.8</sub>Fe<sub>4.0</sub>)A<sub>l0.8</sub>(PO<sub>4</sub>)<sub>12</sub>(OH)<sub>2</sub>

    ```
    TITL DICKINSONITE C2/c K0.5Li.2Na5.4Ca0.9(Mn9.8Fe4.0)Al0.8(PO4)12(OH)2
    CELL 0.43460 16.62506 10.0747 24.8365 90.000 105.24066 90.000
    ZERR 1        0.001    0.001  0.001   0.0     0.01     0.0
    LATT 7
    SYMM X, -Y, Z+1/2
    SFAC MN CA FE P AL NA  O K F
    UNIT 56  4 4 48  4 12 200 1 1
    L.S. 100
    FMAP 2
    BOND 0.5
    PLAN 25
    LIST 5
    DFIX 51 0.01 P1 O1A P1 O1B P1 O1C P1 O1D
    DFIX 51 0.01 P2 O2A P2 O2B P2 O2C P2 O2D
    DFIX 51 0.01 P3 O3A P3 O3B P3 O3C P3 O3D
    DFIX 51 0.01 P4 O4A P4 O4B P4 O4C P4 O4D
    DFIX 51 0.01 P5 O5A P5 O5B P5 O5C P5 O5D
    DFIX 51 0.01 P6 O6A P6 O6B P6 O6C P6 O6D
    DFIX 51.633 0.05 O1A O1B O1A O1C O1A O1D O1B O1C O1B O1D O1C O1D
    DFIX 51.633 0.05 O2A O2B O2A O2C O2A O2D O2B O2C O2B O2D O2C O2D
    DFIX 51.633 0.05 O3A O3B O3A O3C O3A O3D O3B O3C O3B O3D O3C O3D
    DFIX 51.633 0.05 O4A O4B O4A O4C O4A O4D O4B O4C O4B O4D O4C O4D
    DFIX 51.633 0.05 O5A O5B O5A O5C O5A O5D O5B O5C O5B O5D O5C O5D
    DFIX 51.633 0.05 O6A O6B O6A O6C O6A O6D O6B O6C O6B O6D O6C O6D
    WGHT 0.133500 3082.684570
    FVAR 0.05862  0.01182  0.01200  0.00788  1.55710
    NA1  6  10.000000  10.500000  10.000000   10.50000   0.02762
    NA2  6  10.250000  10.250000  10.000000   10.50000   0.02420
    K3   8  10.000000   0.005378  10.250000   10.50000   0.00001
    NA4  6   0.132936   1.015633   0.119276    0.67743  41.00000
    OH1  9   0.233381   0.503695   0.139281   11.00000  21.00000
    AL1  5  10.000000  10.000000  10.000000   10.50000  21.00000
    P1   4   0.101234    0.266482  0.048055   11.00000  21.00000
    O1A  7   0.046363    0.140677  0.046868   11.00000  21.00000
    O1B  7   0.065798    0.375386  0.080061   11.00000  21.00000
    O1C  7   0.094964    0.318291 -0.011999   11.00000  21.00000
    O1D  7   0.194034    0.236335  0.077738   11.00000  21.00000
    P2   4   0.124733    0.749571  0.037486   11.00000  21.00000
    O2A  7   0.104062    0.687555 -0.021989   11.00000  21.00000
    O2B  7   0.221242    0.751802  0.060897   11.00000  21.00000
    O2C  7   0.083064    0.665482  0.075269   11.00000  21.00000
    O2D  7   0.092371    0.894892  0.035196   11.00000  21.00000
    P3   4   0.369061    0.466728  0.076381   11.00000  21.00000
    O3A  7   0.447457    0.449182  0.053776   11.00000  21.00000
    O3B  7   0.368578    0.345043  0.114996   11.00000  21.00000
    O3C  7   0.376093    0.599224  0.109382   11.00000  21.00000
    O3D  7   0.287863    0.465437  0.027768   11.00000  21.00000
    P4   4   0.127794    0.225436  0.211515   11.00000  21.00000
    O4A  7   0.126217    0.174766  0.270341   11.00000  21.00000
    O4B  7   0.201643    0.158136  0.194396   11.00000  21.00000
    O4C  7   0.134268    0.379504  0.210496   11.00000  21.00000
    O4D  7   0.044440    0.183265  0.169512   11.00000  21.00000
    P5   4   0.355820    0.301319  0.296438   11.00000  21.00000
    O5A  7   0.339577    0.359069  0.236380   11.00000  21.00000
    O5B  7   0.441640    0.345068  0.334858   11.00000  21.00000
    O5C  7   0.283200    0.353624  0.319324   11.00000  21.00000
    O5D  7   0.351220    0.146694  0.294494   11.00000  21.00000
    P6   4   0.383165    1.014769  0.131351   11.00000  21.00000
    O6A  7   0.472475    1.064962  0.132976   11.00000  21.00000
    O6B  7   0.385972    0.860560  0.137727   11.00000  21.00000
    O6C  7   0.353089    1.080269  0.179339   11.00000  21.00000
    O6D  7   0.318630    1.048330  0.075051   11.00000  21.00000
    A1   1   0.019823    0.750835  0.402829    0.99006  41.00000
    A2   1   0.105462    0.515578  0.139693    0.98645  41.00000
    A3   1   0.465396    0.744221  0.100429    1.00000  41.00000
    A5   1   0.214548    0.509123  0.267225    0.99424  41.00000
    A6   1   0.295040    0.700975  0.147117    0.94509  41.00000
    A7   1   0.280683    0.706379  0.658489    0.96677  41.00000
    A8   1   0.219351    0.602265 -0.024549    0.63835  41.00000
    HKLF 4
    REM  DICKINSONITE C2/c K0.5Li.2Na5.4Ca0.9(Mn9.8Fe4.0)Al0.8(PO4)12(OH)2
    REM R1 = 0.1331 for   907 Fo \> 4sig(Fo) and 0.1340 for all   916 data
    REM   134 parameters refined using 60 restraints
    END 
    WGHT     0.0861  3480.0105 
    REM Highest difference peak 4.708, deepest hole -2.539, 1-sigma level 0.454
    Q1   1  0.2244 0.2857 -0.0214 11.00000 0.05   4.71
    Q2   1  0.5000 1.1384 0.2500 10.50000 0.05   2.97
    Q3   1  0.4261 0.9968 0.1663 11.00000 0.05   2.69
    Q4   1  0.0019 -0.0013 0.2731 11.00000 0.05   2.69
    Q5   1  0.1778 1.0025 0.1109 11.00000 0.05   2.62
    Q6   1  0.0000 0.4305 0.2500 10.50000 0.05   1.76
    Q7   1  0.2347 0.7048 -0.0272 11.00000 0.05   1.58
    Q8   1  0.0765 0.4784 0.2261 11.00000 0.05   1.55
    Q9   1  0.2033 0.5138 -0.0425 11.00000 0.05   1.42
    ```

    Perspective view of the refined unit cell contents of dickinsonite (refined in C2/c). Polyhedra colors: P (green), Na (yellow), K (cyan), Al (blue) and Mn/Fe (violet). Removal the center of symmetry (space group Cc) caused the R<sub>1</sub> value to drop to 0.089. 

    ![](.//Pictures/100000000000051400000291804457D499A91892.png)

### Load tts-INCO SOL/PCS files

Here the output files from *tts-INCO* can be opened to display the reflections on the image for the multiple solutions. If there are more than one, can be selected simultaneously (painted in different colors) to check if there are multiple grains. HKL indexes can be shown, peaks can be added (activating the option add peaks to click on the image) or removed (delete from list or by right-clicking).

![](.//Pictures/1000000000000314000001B219F9D84E6D95386E.png)![](.//Pictures/10000000000003DD000001699BEE3FD5DEFAD1FF.png)

Also the peaks can be integrated by clicking *extract intensities* and a window equivalent as the one in find/integrate peaks will be opened.

### Load XDS file

This option is similar as the previous one but with a spot.xds file from *XDS* (**X**-ray **D**etector **S**oftware, CCP4; Kabsch, 1988).

### SC data to INCO

This option is used to sum image files specifying ranges of measurement.

![](.//Pictures/10000000000002F300000225F4D8F9B93322A514.png)

For example, taking single crystal data collected from -15º to 15º rotation with steps of 0.25º (120 images), it can be converted to 3 images of 15º rotation each with overlapping of 7.5º. In this case, 3 images will be generated: 1) data from -15 to 0º; 2) data from -7.5 to 7.5º; 3) data from 0 to 15º. Usually if you have single crystal data there is no need to perform any sum of the data, this is only intended in case tts_INCO wants to be used for any specific reason or to check data by performing other combinations of sums. Optionally, a background file can be subtracted to all the individual files before adding them up.

## 5. Phase ID

This is a strong part of *d2Dplot* which allows the fast identification of compounds from a custom database. There are two different “compound databases” considered in the program (actually lists will be the correct term to refer to them and not database). The full database, which is accessed via *Phase_ID - Database* menu or the *Database* button in the main window, and the QuickList database which is intended to be a much smaller one (a selection of compounds from the full database) and that can be accessed directly from the main window. The “databases” (or lists) are stored in plain-text files and the paths for the default ones that are automatically considered by the program are given in the `d2dconfig.cfg` file.

### Database

![](.//Pictures/100000000000027D00000251B6524DB6730DA2ED.png)

Here a plain-text DB file can be loaded. By default it opens the file `default.db` (which is in the program folder) as the example one coming with *d2Dplot*. Once loaded:

  - Click on any compound to see the expected diffraction rings position on the image (if *ShowRings* is selected)

    ![](.//Pictures/1000000000000361000001C2294598800556F97F.png)

  - Check *apply name filter* and type to easily find the desired compound

  - Any selected compound may be edited from the fields on the right section of the window and clicking *apply changes* to update it or *add as new* to copy it as a new entry. Also new compounds may be added or removed by clicking *new* or *remove*. For new compound the information should be introduced. If the unit cell and space groups are known, the expected reflection positions can be calculated with *calc Refl* and the hkl list will be updated automatically. Alternatively, an HKL file or a CIF file can be imported. For CIF files, the hkl list (with calculated structure factors) will be automatically generated taking the cell parameters, symmetry and atom positions from the file. A confirmation window will show the information retrieved from the CIF to check for correctness.
    
    ![](.//Pictures/100000000000026E0000014C02B5454E229FA258.png)

  - Alternatively you can edit manually the DB file. It is a simple self-explanatory text file and its format is explained in another section of this guide.

There is the possibility to search in the database by image peak positions (selected on the main image window by the select points tool). To search by peaks:

  - On the main window click on the desired rings so that they are selected in the point list (*Sel.points* should be active)

  - Click the button *search by peaks.* There are two options that affect the result of the search:
    
      - To consider the total number of reflections up to the *d*-spacing corresponding to the last input ring (recommended).
      - To consider the intensity of the rings (not recommended, only if the rings are well defined, the database contain intensity info and the first option did not gave good results).

  - List will be updated by the best matching compounds (with respective residuals)

  - Click on the compounds to see the rings on top of your image and check if there is a good match.

    ![](.//Pictures/100000000000040F000001F1C6A610F6DD35DFD1.png)

**Note**: The purpose of this database system in *d2Dplot* is to allow you (the user of the program) creating your own database with your choice of compounds (e.g. the family of compounds you are working with as possible candidates for phase identification). There are several compound databases where you can find X-ray diffraction information, including *d*-spacings to introduce to your *d2Dplot* database. These databases can be proprietary ([ICDD](http://www.icdd.com/), [ICSD](https://icsd.fiz-karlsruhe.de/), [CCDC](http://www.ccdc.cam.ac.uk/),...) so that you need to purchase a license, or free ([COD](http://www.crystallography.net/)). The author of *d2Dplot* takes no responsibilities regarding where the final users of the program gets the X-ray diffraction information or its correctness.

The default DB is a small selection of 60 compounds taken from different sources. Each entry contains the reference from where it has been taken (with the respective authors) which can be retrieved from the *reference* field on each entry of the database. If any of these entries should be removed (for whatever reason) please contact the author and they will be removed immediately.

## 6. *Macro* mode

*Macro* mode is the way to make the program do some operations to images and get directly the output, without opening any GUI if possible. The *macro* mode can be accessed via the command line. It is accessed by entering *-macro* as first argument when launching the program from the command line followed by the filename to the image to be processed. Then following arguments are available:

  - **sol**    
    Displays directly a tts-inco SOL file (same filename as the input image).

  - **rint \[CALfile\] \[-outdat DATfile\]**    
    Performs radial integration.
    If no CALfile is specified, calibration parameters are taken from the image header.
    If no DATfile is specified, same name as the input image (but .dat) is used.

  - **cal 0/1/2... \[dist\] \[wave\] \[-outcal \[CALfile\]\]**    
    Instrumental Parameters Calibration.

    The first argument following -cal is an integer to select the calibrant substance. It corresponds to: 0= LaB6, 1= Si, 2= first calibrant in config file, 3= second calibrant in cfg file, etc...");

    If no dist or wave are specified they are taken from the image header.

    Add -outcal option to generate a CAL filename with the same name as the input image as long as no CALfile is specified.

  - **show**   
    To open graphical display and do not exit after processing.     
    (if the first argument is *-help* then the different options are listed)     
    (as it has been said before, if the only command line argument is a path to an image it will be opened directly)


Examples:

```
./d2Dplot -macro lab6_180_0003.edf -cal 0 180 0.3187 -outcal lab6_180.cal`

Running on Unix or Linux
[19:25] 10 vava33.d2dplot.D2Dplot_global [CONFIG] ** LOGGING
DISABLED **
Console logging DISABLED
[19:28] MACRO MODE ON
[19:28] Reading img file: lab6_180_capillary_0003.edf
[19:28] CAL option found, performing LaB6 calibration
[19:28] Using entered distance 180.000
[19:28] Using entered wavelength 0.3187
--------------------------------------------------------------------------------
[19:28] REFINEMENT RESULTS:
--------------------------------------------------------------------------------
CenterX=1023.43250
CenterY=1023.45001
S-D_distance=181.57628
ROT=35.75453
TILT=-1.25287
--------------------------------------------------------------------------------
[19:28] Writting output CAL file: lab6_180.cal
```

``` 
./d2Dplot -macro lab6_180_0003.edf -rint
lab6_180.cal
Running on Unix or Linux
[19:25] 10 vava33.d2dplot.D2Dplot_global [CONFIG] ** LOGGING
DISABLED **
Console logging DISABLED
[19:26] MACRO MODE ON
[19:26] Reading img file: lab6_180_0003.edf
[19:26] RINT option found, performing Radial Integration
[19:26] Using integration parameters from CAL file: lab6_180.inp
[19:26] 
[19:26]   x-beam center: 1023.430
[19:26]   y-beam center: 1023.450
[19:26]   distance:     181.576
[19:26]   wavelength:   0.3187
[19:26]   tilt rotation: 35.8
[19:26]   angle of tilt: -1.25
[19:26] 
[19:26]   t2ini:     0.000
[19:26]   t2fin:     23.866
[19:26]   stepsize:  0.0236
[19:26]   start azim: 0.0
[19:26]   end azim:  360.0
[19:26]   subadu:    -9.5
[19:26] 
[19:26] Writting output DAT file: lab6_180_0003.dat
```
 

## 7. Image formats info

### D2D format

This is a ASCII-header *d2Dplot* format followed by a binary data part using and encoding of unsigned shorts (2-byte little-endian unsigned integers), similar to EDF or IMG formats but with custom header items. It looks like this:

```
{
ByteOrder = LowByteFirst
DataType = UnsignedShort
DataSize = 8388608
Dim_1 = 2048
Dim_2 = 2048
Beam_center_x = 1023.66
Beam_center_y = 1024.22
Pixelsize_x = 79.00
Pixelsize_y = 79.00
Ref_distance = 199.61
Ref_wave = 0.4246
Det_tiltDeg = 0.000
Det_rotDeg = 0.000
Scan_omegaIni = 0.0
Scan_omegaFin = 0.0
Scan_acqTime = -1.0
EXZMargin =0
EXZThreshold =1
EXZdetRadius=1024
EXZpol1 =976 982 957 1013 964 1048 986 1073 1016 1081 1059 1075 10851039 1088 998 1057 969 1005 958 313 263 292 282
EXZarc1=1325 1067 52 14
EXZarc2=507 1167 33 8
}
(binary data)
```

### BIN format

This is a pure binary *d2Dplot* format. There are 60 bytes of header
followed by the diffraction data (starting at byte 61) using an encoding
of signed shorts (2-byte little-endian signed integers). The header info
is:

|                   |                                         |
| ----------------- | --------------------------------------- |
| Integer (4 bytes) | dimension X (image “columns”) in pixels |
| Integer (4 bytes) | dimension Y (image “rows”) in pixels    |
| Real (4 bytes)    | Image scale factor                      |
| Real (4 bytes)    | Beam X (in pixels)                      |
| Real (4 bytes)    | Beam Y (in pixels)                      |
| Real (4 bytes)    | Pixel size X (microns)                  |
| Real (4 bytes)    | Pixel size X (microns)                  |
| Real (4 bytes)    | Sample-to-detector distance (mm)        |
| Real (4 bytes)    | Wavelength (Angstrom)                   |
| Real (4 bytes)    | Omega initial (degrees)                 |
| Real (4 bytes)    | Omega final (degrees)                   |
| Real (4 bytes)    | Acquisition time (seconds)              |

### EDF format

ESRF Data Format. (search the ESRF web page for more info, e.g.[http://www.esrf.eu/computing/scientific/SAXS/doc/SaxsKeywords/SaxsKeywords.pdf](http://www.esrf.eu/%20computing/scientific/SAXS/doc/SaxsKeywords/SaxsKeywords.pdf)). There are different implementations of the format, the one supported by *d2Dplot* looks like this:

```
{
HeaderID = EH:000001:000000:000000 ;
ByteOrder = LowByteFirst ;
DataType = UnsignedShort ;
Size = 8388608 ;
Dim_1 = 2048 ;
Dim_2 = 2048 ;
beam_center_x = 1023.66 ;
beam_center_y = 1024.22 ;
pixelsize_x = 79.00 ;
pixelsize_y = 79.00 ;
ref_distance = 199.61 ;
ref_wave = 0.4246 ;
scan_type = mar_ct (-1.0,) ;
}
(binary data)
```

### IMG format

ADSC-style IMG files \[Arvai, A. J., & Nielsen, C. (1983). ADSC Quantum-210 ADX\]. The ones supported looks like this:

```
{
HEADER_BYTES= 512;
TYPE=unsigned_short ;
BYTE_ORDER=little_endian;
SIZE1=2048;
SIZE2=2048;
DISTANCE= 199.610 ;
PIXEL_SIZE= 0.079000 ;
WAVELENGTH=0.424600;
BEAM_CENTER_X=80.87;
BEAM_CENTER_Y=80.91;
}
(binary data)
```

### GFRM format

Bruker, A. X. S. Area Detector Frame Format \[e.g. GADDS detector, Bruker, A. X. S. "General Area Detector Diffraction System (GADDS) User Manual." *Madison, WI* 4 (1999)\]

### SPR format

“Spreadsheet” format. Table of intensities in ASCII format with the image pixel size (X Y) in the first line.

```
2048   2048 
1.78000E+02 1.61000E+02 1.73000E+02 1.86000E+02 2.23000E+02 2.57000E+02
... (... 2048 columns)
1.23000E+02 2.36000E+02 1.77000E+02 1.56000E+02 1.88000E+02 2.56000E+02
... (... 2048 columns)
... (2048 rows).
```

### TIFF format

TIF image format.

### CBF format

DECTRIS Pilatus (Henrich *et al.* 2009) image format.
\[[https://www.dectris.com/](https://www.dectris.com/)\]

*d2Dplot* only supports the following implementation of CBF:

  - Compression: CBF_BYTE_OFFSET 
  - Content-Transfer-Encoding: BINARY
  - X-Binary-Element-Type: “signed 32-bit integer”
  - X-Binary-Element-Byte-Order: LITTLE_ENDIAN

## 8. Other file formats info

### Database (DB) format

The database files (*.DB) contain crystallographic information of compounds. They are plain text files with an entry like this one for each of the compounds:

```
#COMP: Lanthanum hexaboride
#NAMEALT: here alternative names can be introduced (will be used in the name search filter)
#NAMEALT: there can be more than one line like this
#FORMULA: La B6
#CELL_PARAMETERS: 4.1569 4.1569 4.1569 90.000 90.000 90.000
#SPACE_GROUP: P m 3 m
#REF: National Institute of Standards and Technology
#COMMENT: Any comment regarding the entry (temperature, pressure, etc...) can be entered here. 
#COMMENT: Also multiple comment fields can be added.
#LIST: H K L dsp Int
1  0  0  4.15760  13.60
1  1  0  2.93990  21.83
1  1  1  2.40040  42.36
2  0  0  2.07880  56.99
2  1  0  1.85930  11.83
2  1  1  1.69730   5.82
2  2  0  1.46990   0.24
2  2  1  1.38590 100.00
3  0  0  1.38590  54.08
3  1  0  1.31470  67.94
3  1  1  1.25360   4.49
```

Different compounds are separated by a blank line. 

The only required field are the compound name (#COMP) and the d-spacing list (#LIST), which can be also introduced without intensities.

Compounds in the database can be added manually with a text editor or by using the database module of *d2Dplot* (add/edit compound).

### Excluded zone (EXZ) format

The excluded zone file itself have comments explaining the three possible fields defining excluded zones. It looks like this:

```
! Excluded zones file for: /home/ovallcorba/lab6_29p2_200_coll_0000.edf
EXZmargin=0
EXZthreshold=0
EXZdetRadius=1024
EXZpol1=997 581 889 385 646 530 847 510 855 592
EXZarc1=1325 1067 52 14
!
! EXZmargin    Margin of the image in pixels (if any)
! EXZthreshold Pixels with Y\<threshold will be excluded
! EXZdetRadius To exclude corners of the image in case detection area is circular(radius in px)
! EXZpol   # Sequence of pixels (X1 Y1 X2 Y2 X3 Y3...) defining a polygonal shape
! EXZarc   # Arc-shape defined as: ArcCenterX ArcCenterY ArcHalfRadialWthPx ArcHalfAzimWthDeg
```

## 9. References

A. Hammersley, S. Svensson, A. Thompson. Calibration and correction of spatial distortions in 2D detector systems. *Nucl. Instr. Meth.* **1994**, 346, 312–321.

M.L. Hart, M. Drakopoulos, C. Reinhard, & T. Connolley. Complete elliptical ring geometry provides energy and instrument calibration for synchrotron-based two-dimensional X-ray diffraction. *J. Appl. Crystallogr.*, **2013**, 46, 1249–1260.

B. Henrich, A. Bergamaschi, C. Broennimann, R. Dinapoli, E.F. Eikenberry, I. Johnson, M. Kobas, P. Kraft, A. Mozzanica, B. Schmitt. PILATUS: A single photon counting pixel detector for X-ray applications. *Nucl Instrum Meth A*. **2009**, 607, 247–249.

B. Hinrichsen, R.E. Dinnebier & M. Jansen. Two-dimensional Diffraction Using Area Detectors. In: *Powder diffraction: theory and practice*, **2008**. Royal Society of Chemistry.

W. Kabsch. Evaluation of single-crystal X-ray diffraction data from a position-sensitive detector. *J. Appl. Crystallogr.* **1998**, 21, 916–924.

J. Rius, O. Vallcorba, C. Frontera, I. Peral, A. Crespi, C. Miravitlles. Application of synchrotron through-the-substrate microdiffraction to crystals in polished thin sections. *IUCrJ*, **2015**, 2, 452–463.

J. Rius, O. Vallcorba, C. Frontera. *TTS_software: A computer software for crystal structure analysis from tts microdiffraction data*. Institut de Ciència de Materials de Barcelona, CSIC, (Spain) **2016**. Available at [http://](http://departments.icmab.es/crystallography/software)[departments.icmab.es/crystallography/software](http://departments.icmab.es/crystallography/software).

G.M. Sheldrick. Crystal structure refinement with SHELXL. *Acta Cryst. Section C: Structural Chemistry*, **2015**, 71, 3–8.

## 10. Miscellaneous

### Release notes

*d2Dplot* development started in 2013 as a tool to visualize the orientation search results of microvolumes (*tts-* INCO and related methodologies, [Rius *et al.* IUCrJ. 2015; 2, 452-463] (http://journals.iucr.org/m/issues/2015/04/00/fc5010/index.html)) and as complement to develop the technique. The program has grown a lot since then, and while it remains basically a tool to visualize diffraction images it may be useful and interesting for a general usage. This is why after a little polishing it has been made available for use.

Feedback to the author would be greatly appreciated. Also, if you find interesting to add a certain functionality ask me and I will try my best.

*d2Dplot* is completely programmed with Java<sup>TM</sup> ([www.java.com](http://www.java.com/)) using OpenJDK version 11.0.9.1 (GNU General Public License, version 2, with the Classpath Exception: <https://openjdk.java.net/legal/gplv2+ce.html>). You may find Oracle's free, GPL-licensed, production-ready OpenJDK binaries necessary to run *d1Dplot* at <https://openjdk.java.net/>.

The following 3<sup>rd</sup> party libraries have been used:

  - Commons Math. https://commons.apache.org/proper/commons-math/   
    Apache License: http://www.apache.org/licenses/LICENSE-2.0

  - MigLayout. http://www.miglayout.com/   
    BSD license: http://directory.fsf.org/wiki/License:BSD_4Clause

  - ImageJ 1.50i. https://imagej.nih.gov/ij/index.html   
    Public-domain: https://imagej.net/Licensing.

(No changes on the source codes of these libraries have been made, you can download the source codes for these libraries at their respective websites).

Major changes in the last version (2101):

  - Removed QuickList. Database module now allows to select up to 4 compounds to display their ring positions at the same time. Added the possibility to tag compounds as favorite to have a fast selection of compounds. 
  - Added the possibility to write image as SPR format
  - Added command line option for azimuthal plot.

### Contact information

<div style="text-align: center;">

![](.//Pictures/1000000100000320000001B78A3B14ECB5904E4F.png)

**Oriol Vallcorba**   
ALBA Synchrotron Light Source - CELLS (http://www.cells.es)    
Carrer de la Llum 2-26, 08290 Cerdanyola del Vallès, Barcelona (Spain)   
Phone: +34 93 592 4363    
e-mail: ovallcorba@cells.es
</div>

### Conditions of use

From August 2022 the program is open source and licensed under the GPL-3.0 license \[https://www.gnu.org/licenses/gpl-3.0.en.html\].

Citation of O. Vallcorba & J. Rius. *d2Dplot*: 2D X-ray diffraction data processing and analysis for through-the-substrate microdiffraction *J. Appl. Cryst.* **2019**, 52, 478–484 would be greatly appreciated when this program helped to your work.

### Disclaimer

This software is distributed WITHOUT ANY WARRANTY. The authors (or their institutions) have no liabilities in respect of errors in the software, in the documentation and in any consequence of erroneous results or damages arising out of the use or inability to use this software. Use it at your own risk. 

The purpose of the database system implemented in the program is the creation of a personal compound database by the users. The authors of the program (or their institutions) take no responsibilities in respect of where the data is taken from or its correctness. The default DB is a small selection of 60 compounds coming from different sources. Each entry contains the reference from where it has been taken (with the respective authors). If any of these entries should be removed (for whatever reason) please contact the author and they will be removed immediately.

### Acknowledgments

Thanks are due the Spanish "Ministerio de Economía y Competitividad", to the "Generalitat the Catalunya" and to ALBA Synchrotron for continued financial support (Projects: MAT2015-67593P, MAT2012-35247, ALBA-IH2015MSPD).


---

**Copyright © Oriol Vallcorba 2013**

(document last revision on August 21st, 2022)
