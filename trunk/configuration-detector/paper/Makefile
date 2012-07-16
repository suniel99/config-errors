
PDF_FIGURES = architecture.pdf

all: main.ps main.pdf

clean:
	rm main.pdf
	rm main.dvi
	rm main.aux
	rm main.blg
	rm main.log
	rm main.bbl

main.dvi:
	latex main.tex
	bibtex main
	latex main.tex
	latex main.tex

main.ps: main.dvi
	dvips main

main.pdf: main.dvi
	pdflatex main

TAGS: tags
tags: 
	etags `latex-process-inputs -list main.tex`
