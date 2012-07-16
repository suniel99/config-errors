MAIN_FILE = main

TEX_FILES = \
main.tex \
macros.tex \
abstract.tex \
introduction.tex \
technique.tex \
propagation.tex \
profiling.tex \
deviation.tex \
implementation.tex \
evaluation.tex \
related.tex \
conclusion.tex

PDF_FIGURES = architecture.pdf

all: ${MAIN_FILE}.pdf

force:
	rm -f ${MAIN_FILE}.pdf
	${MAKE}

clean:
	rm -f ${MAIN_FILE}.pdf
	rm -f ${MAIN_FILE}.dvi
	rm -f ${MAIN_FILE}.aux
	rm -f ${MAIN_FILE}.blg
	rm -f ${MAIN_FILE}.log
	rm -f ${MAIN_FILE}.bbl

${MAIN_FILE}.dvi: ${TEX_FILES}
	latex ${MAIN_FILE}.tex
	bibtex ${MAIN_FILE}
	latex ${MAIN_FILE}.tex
	latex ${MAIN_FILE}.tex

${MAIN_FILE}.ps: ${MAIN_FILE}.dvi
	dvips ${MAIN_FILE}

${MAIN_FILE}.pdf: ${TEX_FILES} ${PDF_FIGURES}
	pdflatex ${MAIN_FILE}
	bibtex ${MAIN_FILE}
	pdflatex ${MAIN_FILE}
	pdflatex ${MAIN_FILE}

TAGS: tags
tags: 
	etags `latex-process-inputs -list ${MAIN_FILE}.tex`
