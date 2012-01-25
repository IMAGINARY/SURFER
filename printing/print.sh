#!/bin/sh

script_dir=$(dirname $0)

echo
echo Printing ...
echo script_dir $script_dir
cd $script_dir

pdflatex -interaction=nonstopmode surfer_print.tex
lpr surfer_print.pdf

# move surf input and script output to archive and clean up
timestamp=`date +%Y-%m-%d_%H-%M-%S`
mkdir -p archive
mv surfer_print.pdf archive/$timestamp.pdf
mv print_tmp.png archive/$timestamp.png
mv print_tmp.jsurf archive/$timestamp.jsurf
rm print_tmp.tex

echo done ...
