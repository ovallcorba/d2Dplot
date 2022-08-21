# d2Dplot

d2Dplot is a plotting program for 2D X-ray diffraction (XRD) data. Its main purposes are to be user friendly, to provide specific tools to manage 2D XRD data. The basic processing available for 2D XRD data includes the conversion between data formats, summation and subtraction of frames, background estimation, definition of excluded zones, instrumental parameters calibration, the conversion to 1D XRD data and the generation of azimuthal plots. It also includes a set of tools for the application of the tts-*μ*XRD metodology with peak finding and integration capabilities and a frontend to the [tts_software](https://scripts.iucr.org/cgi-bin/paper?S2052252515007794). The program also includes a compound database for easy phase identification directly on the 2D image.

Detailed information can be found in the users guide ([pdf](d2Dplot_userguide.pdf) | [markdown](./docs/d2Dplot_userguide.md)) or in the publications:
- d2Dplot: 2D X-ray diffraction data processing and analysis for through-the-substrate microdiffraction [*Journal of Applied Crystallography* (2019), 52, 478-484](https://scripts.iucr.org/cgi-bin/paper?S160057671900219X)
-  XRD data visualization, processing and analysis with d1Dplot and d2Dplot software packages [*Multidisciplinary Digital Publishing Institute Proceedings* (2020), 62 (1), 9](https://www.mdpi.com/2504-3900/62/1/9)

*d2Dplot* development started in 2013 as a tool to visualize the orientation search results of microvolumes ([tts_software](https://scripts.iucr.org/cgi-bin/paper?S2052252515007794) and related methodologies), and as complement to develop the technique. The program started to grow quite a lot since then and became a tool to visualize diffraction images, useful for other general uses. It was a personal closed source project actively maintained until 2020. From August 2022 the program is open source and licensed under the GPL-3.0 license. Sorry if the code is not compliant with standards, is a little bit messy and contains comments in catalan. Feedback to the author would be greatly appreciated.

### Dependencies

*d2Dplot* is completely programmed with [Java<sup>TM</sup>](http://www.java.com/)) using OpenJDK version 11.0.9.1 (GNU General Public License, version 2, with the Classpath Exception: https://openjdk.java.net/legal/gplv2+ce.html). You may find Oracle's free, GPL-licensed, production-ready OpenJDK binaries necessary to run *d1Dplot* at <https://openjdk.java.net/>.

The following 3rd party libraries have been used:
  - Commons Math. https://commons.apache.org/proper/commons-math/   
    Apache License: http://www.apache.org/licenses/LICENSE-2.0
  - MigLayout. http://www.miglayout.com/   
    BSD license: http://directory.fsf.org/wiki/License:BSD_4Clause
  - ImageJ 1.50i. https://imagej.nih.gov/ij/index.html   
    Public-domain: https://imagej.net/Licensing.

(No changes on the source codes of these libraries have been made, you can download the source codes for these libraries at their respective websites).

The program also uses the following libraries from the same author (packages com.vava33.*)

- vavaUtils (jutils and cellsymm). https://github.com/ovallcorba/vavaUtils

### Installation and use

Binaries for windows and linux can be downloaded in the releases section (https://github.com/ovallcorba/D2Dplot/releases). Otherwise you need to clone the project, gather the dependencies and generate the jar files.

## Authors

  - **Oriol Vallcorba** (ovallcorba@cells.es)

## Disclaimer

This software is distributed WITHOUT ANY WARRANTY. The authors (or their institutions) have no liabilities in respect of errors in the software, in the documentation and in any consequence of erroneous results or damages arising out of the use or inability to use this software. Use it at your own risk.

The purpose of the database system implemented in the program is the creation of a personal compound database by the users. The authors of the program (or their institutions) take no responsibilities in respect of where the data is taken from or its correctness. The default DB is a small selection of 60 compounds coming from different sources. Each entry contains the reference from where it has been taken (with the respective authors). If any of these entries should be removed (for whatever reason) please contact the author and they will be removed immediately.

### Acknowledgments

Thanks are due the Spanish "Ministerio de Economía y Competitividad", to the "Generalitat the Catalunya" and to ALBA Synchrotron for continued financial support (Projects: MAT2015-67593P, MAT2012-35247, ALBA-IH2015MSPD).

## License

This project is licensed under the [GPL-3.0 license](LICENSE.txt)

Citation of the author/program/affiliation, e.g. O.Vallcorba & J.Rius. 2D X-ray diffraction data processing and analysis for through-the-substrate microdiffraction (doi:10.1107/S160057671900219X) or O.Vallcorba & J.Rius. XRD data visualization, processing and analysis with d1Dplot and d2Dplot software packages (doi:10.3390/IOCC_2020-07311), would be greatly appreciated when this program helped to your work.

---
Copyright © Oriol Vallcorba 2013
