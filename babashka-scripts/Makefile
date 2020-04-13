fonts-to-svg:
	bb --time fonttosvg.clj ~/WIP/Krim/kasturi/raw-serif-fonts ~/WIP/Krim/kasturi/raw-sans-serif-fonts ~/WIP/Krim/kasturi/shelf.kasturi/font-vectors

normalize-names:
	bb --time normalize_svg_name.clj ~/WIP/Krim/kasturi/shelf.kasturi/font-vectors

svgify: fonts-to-svg normalize-names

clearsvgs:
	rm -rf ~/WIP/Krim/kasturi/shelf.kasturi/font-vectors/*
