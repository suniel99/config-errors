
PDF_FIGURES = architecture.pdf

all: main.ps main.pdf

clean:
	rm main.pdf
	rm main.dvi
	rm main.aux
	rm main.blg
	rm main.log
	rm main.bbl

autodoc.dvi:
	latex main.tex
	bibtex main
	latex main.tex
	latex main.tex

autodoc.ps: main.dvi
	dvips main

autodoc.pdf: main.dvi ${PDF_FIGURES}
	pdflatex main

TAGS: tags
tags: 
	etags `latex-process-inputs -list main.tex`
