import at.jku.cdl.ecco.adapter.JavaASTReader;
import at.jku.cdl.ecco.adapter.View.*;
import at.jku.isse.ecco.adapter.AssociationInfo;
import at.jku.isse.ecco.composition.LazyCompositionRootNode;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.gui.view.artifacts.AssociationInfoImpl;
import at.jku.isse.ecco.service.EccoService;
import at.jku.isse.ecco.storage.ser.dao.SerEntityFactory;
import at.jku.isse.ecco.tree.Node;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.*;
import org.opentest4j.AssertionFailedError;


// these tests assume that the provided Content from the viewer has only the backgroundfill of the color
public class ViewerColorTest extends AbstractFxTest {

    private static final Path base =  Paths.get("src","test", "resources").toAbsolutePath();
    private static final Path commitFolders =  base.resolve("commitFolders");
    private static final Path repFolder = base.resolve(".ecco");
    JavaViewer viewer;
    static EccoService service;

    public ViewerColorTest () {
        viewer = new JavaViewer();
    }

    @Test
    public void testDefaultColor(){
        commitExample1();
        Node classPluginNode = getRootNode().getChildren().get(0).getChildren().get(0);
        checkMonoColor(classPluginNode, AbstractJavaNode.defaultColor);
    }

    @Test
    public void testChangingColors() {
        commitExample1();
        List<Color> colors = Arrays.asList(Color.RED,Color.GREEN,Color.BLUE,Color.CYAN);
        for (Color color : colors) {
            testOneColorOneAssociation(color);
        }
    }

    private void testOneColorOneAssociation(Color color)  {
        try {
            Node classPluginNode = getRootNode().getChildren().get(0).getChildren().get(0);
            Collection<AssociationInfo> associationsData = getAssociationsData();
            associationsData.forEach(association -> {
                try {
                    AssociationInfoImpl association1 = (AssociationInfoImpl) association;
                    association1 .colorProperty().set(color);
                }catch (Exception ignored){
                }

            });
            viewer.setAssociationInfos(associationsData);
            checkMonoColor(classPluginNode,color);


        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    @Test
    public void test2Colors() {
        try {
            commitExample2();
            List<Color> colors = Arrays.asList(Color.RED, Color.GREEN);
            LazyCompositionRootNode rootNode = getRootNode();
            Node classPluginNode = rootNode.getChildren().get(0).getChildren().get(0);
            HashMap<Integer, Color> colorMap = new HashMap<>();
            Collection<AssociationInfo> associationsData = getAssosciationDataMultipleColors(colors, colorMap);

            viewer.setAssociationInfos(associationsData);
            Node typeNode = classPluginNode.getChildren().get(1);
            viewer.showTree(typeNode);
            JavaBlockInterface classBlock = viewer.listView.getItems().getFirst();
            VBox vBox = classBlock.getCellContent();

            /*
            for the given commits the relevant children are
            0: signatureLabel
            1: VBox. Vbox(FieldGroup).label of the one Field
            3: Vbox of the constructor with Label(signature) Vbox(content) label(})
            5: Vbox of main method
            7: Vbox of method for feature two
            Rest are spacing Labels
             */

            List<Integer> feature1 = Arrays.asList(0, 1, 3, 5);
            for (int i = 0; i < vBox.getChildren().size(); i++) {
                javafx.scene.Node node = vBox.getChildren().get(i);
                Region pane = (Region) node;
                if (feature1.contains(i)) checkBackgroundColor(pane, colorMap.get(0));
                else if (i == 7) checkBackgroundColor(pane, colorMap.get(1));
            }
        }catch (AssertionFailedError e){
            System.err.println( "-".repeat(10) + "\n" +
                    "Warning for the following AssertionFailedError the ViewerTest test2Colors will fail if the order in which the nodes appear gets changed" +
                    "-".repeat(10) + "\n");
            throw e;
        }
    }


    private void checkBackgroundColor (Region pane, Color color) {
        // as the java blocks return a VBox filled with content its background is null so get Children instead
        try {
            for (javafx.scene.Node node  : pane.getChildrenUnmodifiable()) {
                Region region = (Region) node;
                Background background = region.getBackground();
                if (background == null) {
                    continue;
                }
                List<BackgroundFill> fills = background.getFills();
                BackgroundFill fillToTest = fills.getFirst();
                BackgroundFill backgroundToTest = new BackgroundFill(color, null, null);
                Assertions.assertEquals(fillToTest.getFill(), backgroundToTest.getFill() );
            }
        }catch (Exception e){
            Assertions.fail(e.getMessage());
        }
    };

    private List<AssociationInfo> getAssociationsData(){
        Collection<? extends Association> associations = service.getRepository().getAssociations();
        List<AssociationInfo> associationsData = new ArrayList<>();
        for (Association a : associations) {
           associationsData.add(new AssociationInfoImpl(a));
        }
        return associationsData;
    }

    private Collection<AssociationInfo> getAssosciationDataMultipleColors(List<Color> colors,  HashMap<Integer, Color> colorMap){
        Collection<AssociationInfo> associationsData = getAssociationsData();
        associationsData.forEach(association -> {
            try {
                AssociationInfoImpl association1 = (AssociationInfoImpl) association;
                Color newcolor = colors.get(colorMap.size());
                association1.colorProperty().set(newcolor);
                colorMap.put(colorMap.size(), newcolor);
            }catch (Exception ignored){
            }

        });

        return associationsData;
    }

    private LazyCompositionRootNode getRootNode(){
        Collection<? extends Association> associations = service.getRepository().getAssociations();
        LazyCompositionRootNode rootNode = new LazyCompositionRootNode();
        for (Association association : associations) {
            rootNode.addOrigNode(association.getRootNode());
        }
        return rootNode;
    }
    private void checkMonoColor( Node classPluginNode,Color color) {
        viewer.showTree(classPluginNode);
        ObservableList<JavaBlockInterface> items = viewer.listView.getItems();

        for (JavaBlockInterface item : items) {
            VBox box = item.getCellContent();
            checkBackgroundColor(box, color);
        }
    }

    @Test
    public void test3ColorsAndMixed() {
        try {
            commitExample3();
            List<Color> colors = Arrays.asList(Color.RED, Color.GREEN,Color.BLUE);
            Node classPluginNode = getRootNode().getChildren().get(0).getChildren().get(0);
            HashMap<Integer, Color> colorMap = new HashMap<>();
            Collection<AssociationInfo> associationsData = getAssosciationDataMultipleColors(colors, colorMap);

            viewer.setAssociationInfos(associationsData);
            viewer.showTree(classPluginNode);

            ObservableList<JavaBlockInterface> items =  viewer.listView.getItems();
            for (int i = 0; i < items.size(); i++) {
                JavaBlockInterface item = items.get(i);
                if (i != 0 && item instanceof Statement) { // is import which is only in feature 3 and 0 is skipped since it is from the non existed package
                    checkBackgroundColor(item.getCellContent(),colorMap.get(2));
                }else if (item instanceof TypeDec) {
                    test3HandleTypeDec(item,colorMap);
                }
            }

        }catch (AssertionFailedError e){
            System.err.println( "-".repeat(10) + "\n" +
                    "Warning for the following AssertionFailedError the ViewerTest test3ColorsAndMixed will fail if the order in which the nodes appear gets changed" +
                    "-".repeat(10) + "\n");
            throw e;
        }
    }

    private void test3HandleTypeDec(JavaBlockInterface item,HashMap<Integer, Color> colorMap){
        VBox vBox =  item.getCellContent();
        List<Integer> feature1 = Arrays.asList(0, 1, 3, 5);

                        /*
                        for the given commits the relevant children are
                        0: signatureLabel
                        1: VBox. Vbox(FieldGroup).VBox .label of the two Fields (first is feature one second is feature two  )
                        3: Vbox of the constructor with Label(signature) Vbox(content) label(})
                        3.1 is signature 3.2 VBox with label feature 1 3.3 Vbox with label feature 3 3.4 is label with closing bracket

                        5: Vbox of main method
                        5.1 is signature 5.2 VBox with label feature 1 5.3 Vbox with label feature 3 5.4 is label with closing bracket

                        7: Vbox of method for feature two
                        Rest are spacing Labels
                         */
        for (int j = 0; j < vBox.getChildren().size(); j++) {
            javafx.scene.Node node = vBox.getChildren().get(j);
            Region classRegions = (Region) node;
            switch(j) {
                case 0:
                    checkBackgroundColor( classRegions, colorMap.get(0));

                    break;
                case 1:
                    checkFieldGroupTest3(classRegions,colorMap);
                    break;
                case 3:
                    checkConstructorTest3(classRegions,colorMap);
                    break;
                case 5:
                    checkMainTest3(classRegions,colorMap);
                    break;
                case 7:
                    checkBackgroundColor(classRegions, colorMap.get(1));
                    break;
            }
        }

    }
    private void checkFieldGroupTest3(Region fieldGroupRegion,HashMap<Integer, Color> colorMap) {
        for (int i = 0; i < fieldGroupRegion.getChildrenUnmodifiable().size(); i++) {
            Region childRegion =  (Region) fieldGroupRegion.getChildrenUnmodifiable().get(i);
            if (i == 0)  checkBackgroundColor(childRegion, colorMap.get(0));
            else if (i == 1)  checkBackgroundColor(childRegion, colorMap.get(2));
        }
    }

    private void checkConstructorTest3(Region region,HashMap<Integer, Color> colorMap) {
        List<Integer> feature1 = Arrays.asList(0, 1, 3);

        for (int i = 0; i < region.getChildrenUnmodifiable().size(); i++) {
            Region childRegion =  (Region) region.getChildrenUnmodifiable().get(i);
            if (feature1.contains(i))  checkBackgroundColor(childRegion, colorMap.get(0));
            else if (i == 2)  checkBackgroundColor(childRegion, colorMap.get(2));
        }
    }

    private void checkMainTest3(Region region,HashMap<Integer, Color> colorMap) {
        List<Integer> feature1 = Arrays.asList(0, 1, 3);

        Region fieldgroupVBox = (Region) region.getChildrenUnmodifiable().getFirst();
        for (int i = 0; i < fieldgroupVBox.getChildrenUnmodifiable().size(); i++) {
            Region childRegion =  (Region) fieldgroupVBox.getChildrenUnmodifiable().get(i);
            if (feature1.contains(i))  checkBackgroundColor(childRegion, colorMap.get(0));
            else if (i == 2)  checkBackgroundColor(childRegion, colorMap.get(2));
        }
    }

    private void commitExample1(){
        service.setBaseDir(commitFolders.resolve("Test1"));
        service.commit("test", "TestConfig.1", "ViewerColorTest");
    }

    private void commitExample2(){
        commitExample1();
        service.setBaseDir(commitFolders.resolve("Test2"));
        service.commit("added other feature", "TestConfig.1, AdditionalFeature", "ViewerColorTest");
    }

    private void commitExample3(){
        commitExample2();
        service.setBaseDir(commitFolders.resolve("Test3"));
        service.commit("added yet other feature", "TestConfig.1, AdditionalFeature, ExtraFeature", "ViewerColorTest");
    }



    @BeforeAll
    static void prepareRepository() {
        if (Files.exists(repFolder)) {

            try {
                FileUtils.deleteDirectory(repFolder.toFile());
            } catch (IOException ex) {
                System.out.println("for testing there should not be a folder named .ecco in the the following path " + repFolder + " \n tried to delete it but failed  with : " + ex.getMessage());
                return;
            }
        }
        service = new EccoService();
        service.setRepositoryDir(repFolder);
        Assertions.assertTrue(service.init());
    }

    @AfterAll
    static void deleteRepository() {
        if (Files.exists(commitFolders)) {
            try {
                FileUtils.deleteDirectory(repFolder.toFile());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
