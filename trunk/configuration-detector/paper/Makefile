
PDF_FIGURES = overview.pdf cfg.pdf

all: autodoc.ps autodoc.pdf

clean:
	rm autodoc.pdf
	rm autodoc.dvi
	rm autodoc.aux
	rm autodoc.blg
	rm autodoc.log
	rm autodoc.bbl

autodoc.dvi:
	latex autodoc.tex
	bibtex autodoc
	latex autodoc.tex
	latex autodoc.tex

autodoc.ps: autodoc.dvi
	dvips autodoc

autodoc.pdf: autodoc.dvi ${PDF_FIGURES}
	pdflatex autodoc

overview.pdf: overview.eps
	epstopdf $<

cfg.pdf: cfg.eps
	epstopdf $<

TAGS: tags
tags: 
	etags `latex-process-inputs -list autodoc.tex`
