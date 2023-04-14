package com.example.imagelibrary;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.function.Function;
//cet classe a été écrite afin d'appliquer un filter sur une image
class Filter implements Function<Image, Image> {

    protected String name;
    protected Function<Color, Color> colorMap;

    Filter(String name, Function<Color, Color> colorMap) {
        this.name = name;
        this.colorMap = colorMap;
    }
    //qu'on cet méthode est appellé, applique un filtre sur l'image choisi par l'utilisateur
    @Override
    public Image apply(Image source) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();

//creer une image à partir de l'image choisi en récupérant sa taille
        WritableImage image = new WritableImage(w, h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c1 = source.getPixelReader().getColor(x, y);
                Color c2 = colorMap.apply(c1);

                //changer la couleur de l'image avec le filtre appliquer
                image.getPixelWriter().setColor(x, y, c2);
            }
        }

        return image;
    }
}
