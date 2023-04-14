package com.example.imagelibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

public class LibraryController implements Initializable {
    Gson gson = new Gson();
    FileChooser fileChooser = new FileChooser();
    //liste de transformations
    ArrayList<String> transformationList = new ArrayList<String>();

    @FXML
    private MenuItem symetrieid;
    @FXML
    private MenuItem gbrid;
    @FXML
    private ImageView image;
    @FXML
    private Slider mySlider;
    @FXML
    private Label myLabel;
    @FXML
    private MenuItem sepiaid;
    @FXML
    private MenuItem rotationid;
    @FXML
    private MenuItem btnPrewitmx;
    @FXML
    private MenuItem btnPrewitmy;
    @FXML
    private ImageView imageView;
    @FXML
    private MenuItem item;
    @FXML
    private MenuItem enregistrer;
    @FXML
    private MenuItem encryptImage;
    @FXML
    private MenuBar bar;
    @FXML
    private Menu lesFiltres;
    @FXML
    private TextField textfieldid;
    @FXML
    private Text textid;
    @FXML
    private Button buttonid;

    //fichier json pour écrire les informations sur l'image
    File file = new File("jsonfile.json");

    //Liste pour ajouter un filtre chaque fois qu'il est appliquer
    //cet liste est converti au format json
    List<String> filterClickedList = new ArrayList<String>();

    double[][] filter_Prewit_mx = { { 1.0, 0, -1.0 },
            { 1.0, 0, -1.0 },
            { 1.0, 0, -1.0 } };

    double[][] filter_Prewit_my = { { -1.0, -1.0, -1.0 },
            { 0, 0, 0 },
            { 1.0, 1.0, 1.0 } };

    //liste pour garder les tags
    List<String> tagList = new ArrayList<String>();


    //l'angle de rotation
    int angleR;

    //cet fonction ajout un tag lorsque écrit dans le text field et valider en clickant sur le boutton
    @FXML
    void ajoutTag() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        String txt = textfieldid.getText();
        textid.setText(txt);
        //qu'on un tag est ajouté on l'ajoute dans le fichier json
        try{
            tagList.add(txt);

            fileWriter.write(gson.toJson(tagList));
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.println( "Tags -> "+gson.toJson(tagList));
    }





    //cet fonction retourne la somme des couleures RGB
    private  double valueOf(Color c) {
        return c.getRed() + c.getGreen() + c.getBlue();
    }


    //Lorsqu'on accede au menuItem Filters on appuie sur Color filters et là il y a qlq filtres en plus des autres qui s'afficheront
    // cet fonction cree qlq filtres a appliquer sur l'image
    //elle est lié à la classe Filter
    @FXML
    void createContent(ActionEvent event) throws IOException {

        FileWriter fileWriter = new FileWriter(file);

         List<Filter> filters = Arrays.asList(
                new Filter("Grayscale", c -> c.grayscale()),
                new Filter("Black and White", c -> valueOf(c) < 1.5 ? Color.BLACK : Color.WHITE),
                 new Filter("GBRimg", c -> Color.color(c.getGreen(), c.getBlue(), c.getRed())),
                 new Filter("Invert", Color::invert),
                new Filter("Red", c -> Color.color(1.0, c.getGreen(), c.getBlue())),
                new Filter("Green", c -> Color.color(c.getRed(), 1.0, c.getBlue())),
                new Filter("Blue", c -> Color.color(c.getRed(), c.getGreen(), 1.0))
        );
        lesFiltres = new Menu("PFilters");
        filters.forEach(filterss -> {
            item = new MenuItem(filterss.name);
            item.setOnAction(e -> {
                image.setImage(filterss.apply(image.getImage()));//apply est une methode de la classe Filter qu'on a écrite
                filterClickedList.add(filterss.name);
                String jsonn = gson.toJson(filterClickedList);

                try {
                    fileWriter.write(jsonn);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                System.out.println("liste des Filtres -> " + jsonn);
                    }
            );
            lesFiltres.getItems().add(item);
        });
        bar.getMenus().add(lesFiltres);

    }


    //cette fonction est utilisée pour enregistrer une image
    //on peut enregistrer une image en cliquant ctrl+s
    @FXML
    void enregistrerImage(ActionEvent event) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:derby:\\DB\\testdb;");

        File outputFile = new File("savedIMG.jpg");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image.getImage(), null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    //cet fonction effectue une symetrie d'image
    //pour faire il faut acceder au Transformation est cliquer sur symetrie
    @FXML
    void symetrieAction(ActionEvent event) throws IOException {
        FileWriter fileWriter = new FileWriter(file);

        Gson gson = new GsonBuilder().create();
        Image imageFromFile = image.getImage();
        //pixel reader récupére les données de pixels de l'image choisit par l'utilisateur
        PixelReader pixelReader = imageFromFile.getPixelReader();
        int height = (int) imageFromFile.getHeight();
        int width = (int) imageFromFile.getWidth();
        WritableImage filteredImage = new WritableImage(pixelReader, width, height);
        //pixelwriter est utilisé pourécrire les données de pixel de filteredImage
        PixelWriter pixelWriter = filteredImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixelReader.getArgb(x, y);

                int alpha = ((pixel >> 24) & 0xff);
                int green = ((pixel >> 16) & 0xff);
                int blue = ((pixel >> 8) & 0xff);
                int red = (pixel & 0xff);


                int gbr = (alpha << 24) + (green << 16) + (blue << 8) + red;

                pixelWriter.setArgb(width-1-x, y, gbr);

            }
        }
        image.setImage(filteredImage);
        //ajouter une symetrie dans la liste des transformations lorsqu'une symetrie est effectuer
        transformationList.add("Symetrie");
        String jsonn = gson.toJson(transformationList);
        fileWriter.write(gson.toJson(jsonn));
        fileWriter.close();
        System.out.println("Transformations -> " + jsonn);
    }



    //la fonction imageEncrypt est utilisée pour avoir uneimage modifier
    //on a chiffrer l'image sans utiliser le mot de passe
    //cette fonction prend 30 secondes au maximum pour s'executer
    @FXML
    void imageEncrypt(ActionEvent event) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        Image limage = image.getImage();
        //récupérer les pixeles de l'image
        PixelReader pixelReader = limage.getPixelReader();
        int height = (int) limage.getHeight();
        int width = (int) limage.getWidth();
        //generer une graine à la taille de nombre de pixeles
        byte[] arr = sr.generateSeed(height*width);
        WritableImage filteredImage = new WritableImage(pixelReader, width, height);
        PixelWriter pixelWriter = filteredImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixelReader.getArgb(x, y);

                int alpha = ((pixel >> 24) & 0xff);
                int red = ((pixel >> 16) & 0xff);
                int green = ((pixel >> 8) & 0xff);
                int blue = (pixel & 0xff);
//pour chaque pixel on ajoute son coresspondant dans la liste de byte du seed
                for(int i=0;i< arr.length;i++){
                    if((red+arr[i]<128 ||red+arr[i]>-127)||(green+arr[i]<128 || green+arr[i]>-127)||(blue+arr[i]<128||blue+arr[i]>-127)){
                        int gbr = (alpha << 24) + (red+arr[i] << 16) + (green+arr[i] << 8) + blue+arr[i];
                        pixelWriter.setArgb(x, y, gbr);
                    }
                }
            }
        }
        //afficher le résultat de l'image chiffré dans l'imageView image
        image.setImage(filteredImage);
    }


//cet fonction ajoute une ligne dans la table de la base de données
    void updateSql() throws SQLException {
        //lien pour se connecter dans la base données
        String URL = "jdbc:derby:\\DB\\testdb;";
        String Url ="savedIMG.jpg";
        int Rangle1 = 50;
        String Filters = "[\"GrayScale\",\"Red\"]";
        String Transformations = "Rotation";
        //se connecter à la base de données
        try(Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();){
            stmt.executeUpdate("Drop table Image");
            //requête sql pour insérer une ligne dans une table
            String sql = "insert into Image (Url,Rangle,Filters,Transformations)"
                    +"values(?,?,?,?)";

            PreparedStatement stmt2 = conn.prepareStatement(sql);
            stmt2.setString(1,Url);
            stmt2.setInt(2,Rangle1);
            stmt2.setString(3,Filters);
            stmt2.setString(4,Transformations);
            int rows = stmt2.executeUpdate();
            if(rows>0){
                System.out.println("Image ajoutee au db");
            }
            //stmt.executeUpdate("Drop table Image");
        }
    }



//cet fonction affiche les lignes de la table de la base de données
    void ParcourirSql() throws SQLException {
        String URL = "jdbc:derby:\\DB\\testdb;";
        Connection conn = DriverManager.getConnection(URL);
        try{
            Statement stmt = conn.createStatement();
            String query = "SELECT * from Image";
            ResultSet rs = stmt.executeQuery(query);
//récupérer toutes les lignes de la table
            while(rs.next()) {
                System.out.println("Url = " + rs.getString(1));
                //int Rangle = rs.getInt(2);
                System.out.println(rs.getInt(2));
                String filters = (String)rs.getObject("Filters");
                System.out.println(filters);
                String transformations = rs.getString("Transformations");
                System.out.println(transformations);
            }
            stmt.executeUpdate("Drop table Image");
            conn.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }



//Pour effectuer une rotation on choisit Premièrement l'angle de rotation
// après on l'applique en cliquant sur Rotation dans Transformations
    @FXML
    void rotationFilter(ActionEvent event) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        Gson gson = new GsonBuilder().create();
        RotateTransition rotate = new RotateTransition();
        rotate.setAxis(Rotate.Z_AXIS);
        rotate.setByAngle(angleR);
        rotate.setNode(image);
        rotate.play();
        Trans trans = new Trans("Rotation",angleR);
        transformationList.add(trans.transName);

//ajouter une rotation avec son angle de rotation dans le fichier json
        try{
            gson.toJson(new Trans("Rotation",angleR),fileWriter);
            fileWriter.write(gson.toJson(transformationList));
            fileWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println( gson.toJson(new Trans("Rotation",angleR)));

}



//cet fonction applique le filtre sepia sur l'image choisi par l'utilisateur
    @FXML
    void sepiaFilter(ActionEvent event) throws IOException {
        FileWriter fileWriter = new FileWriter(file);

        SepiaTone sepiaTone = new SepiaTone();
        sepiaTone.setLevel(0.75);
        image.setEffect(sepiaTone);
        filterClickedList.add("SepiaFilter");
        String jsonn = gson.toJson(filterClickedList);
        fileWriter.write(jsonn);
        fileWriter.close();
        System.out.println("liste des Filtres -> " + jsonn);
    }


//cet fonction est utiliser pour afficher l'image choisi à partir d'un fichier
    @FXML
    void getImage(ActionEvent event) throws FileNotFoundException {

        File file = fileChooser.showOpenDialog(new Stage());
        Image imageFromFile = new Image(new FileInputStream(file));
        image.setImage(imageFromFile);
    }



    //Prewit Filter est inspirer d'un github
    //https://github.com/fepalemes/image-edge-detection?fbclid=IwAR392HwYZtb7376rnrxSF1PidxnEBbEgvwMUxb7L-ou0pcIpD8cLPhz6Neg
    //La fonction filter applique le filtre prewit sur l'image choisi par l'utilisateur
    public Image filter(double[][] filter, Image image) {

        if (image == null)

            return null;
        //pixel reader récupére les données de pixels de l'image choisit par l'utilisateur
        PixelReader pixelReader = image.getPixelReader();

        WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        //pixelwriter est utilisé pour écrire les données de pixel de wImage
        PixelWriter pixelWriter = wImage.getPixelWriter();

        for (int readY = 0; readY < image.getHeight(); readY++) {

            for (int readX = 0; readX < image.getWidth(); readX++) {

                double r = 0;

                double g = 0;

                double b = 0;

                for (int i = -1; i < 2; i++) {

                    for (int j = -1; j < 2; j++) {

                        if (readX - i < 0 || readX - i > image.getWidth() - 1 || readY - j < 0

                                || readY - j > image.getHeight() - 1)

                            continue;

                        r += filter[i + 1][j + 1] * pixelReader.getColor(readX - i, readY - j).getRed();

                        g += filter[i + 1][j + 1] * pixelReader.getColor(readX - i, readY - j).getGreen();

                        b += filter[i + 1][j + 1] * pixelReader.getColor(readX - i, readY - j).getBlue();

                    }

                }

                r = (r < 0) ? 0 : r;

                r = (r > 1) ? 1 : r;

                g = (g < 0) ? 0 : g;

                g = (g > 1) ? 1 : g;

                b = (b < 0) ? 0 : b;

                b = (b > 1) ? 1 : b;

                int ir = (int) (r * 255);

                int ig = (int) (g * 255);

                int ib = (int) (b * 255);

                Color c1 = Color.rgb(ir, ig, ib);

                pixelWriter.setColor(readX, readY, c1);

            }

        }

        return wImage;

    }


//cet fonction applique le filtre Prewitt avec le gradient Gx
    @FXML
    void fctnPrewitmx(ActionEvent e) {

        Image img = filter(filter_Prewit_mx, image.getImage());

        if (img != null)
            image.setImage(img);

        filterClickedList.add("PrewitwGx");
        String jsonn = gson.toJson(filterClickedList);
        System.out.println("liste des Filtres -> " + jsonn);

    }


//cet fonction applique le filtre Prewitt avec le gradient Gy
    @FXML
    void fctnPrewitmy(ActionEvent e) {

        Image img = filter(filter_Prewit_my, image.getImage());

        if (img != null)
            image.setImage(img);

        filterClickedList.add("PrewitwGy");
        String jsonn = gson.toJson(filterClickedList);
        System.out.println("liste des Filtres -> " + jsonn);
    }


    //initialize est une fonction de l'interface initializable
    //elle est ppelé pour initialiser le controleur après que l'élement racine est traité
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //pour faciliter le choix de l'image on l'a choisi à partir du fichier pngggg
        fileChooser.setInitialDirectory(new File("src/main/pngggg"));
        //récupérer la valeur du slider
        angleR = (int) mySlider.getValue();
        //afficher l'angle dans le label
        myLabel.setText(angleR + "°");
        //qu'on le slider change de valeur le label change aussi pour donner la valeur exacte
        mySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                angleR = (int) mySlider.getValue();
                myLabel.setText(angleR + "°");

            }
        });


    }
}

