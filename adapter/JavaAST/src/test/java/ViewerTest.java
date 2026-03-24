import at.jku.cdl.ecco.adapter.JavaASTReader;
import at.jku.cdl.ecco.adapter.View.JavaBlockInterface;
import at.jku.cdl.ecco.adapter.View.JavaViewer;
import at.jku.isse.ecco.storage.ser.dao.SerEntityFactory;
import at.jku.isse.ecco.tree.Node;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Set;
import java.util.stream.Collectors;

public class ViewerTest {

    private JavaASTReader reader;
    private final Path base =  Paths.get("src","test", "resources").toAbsolutePath();
    JavaViewer viewer;

    public ViewerTest() {
        reader = new JavaASTReader(new SerEntityFactory());
        viewer = new JavaViewer();
    }

    @Test
    public void JavaEnumTest()
    {
        Path inputFile = Paths.get("EnumTest.java");
        testFileTextEqual(inputFile);
    }

    @Test
    public void ClassTest()
    {
        Path inputFile = Paths.get("ClassTest.java");
        testFileTextEqual(inputFile);
    }


    @Test
    public void ConstructorTest()
    {
        Path inputFile = Paths.get("ConstructorTest.java");
        testFileTextEqual(inputFile);
    }

    @Test
    public void TypeConstructorTest()
    {
        Path inputFile = Paths.get("TypeConstructorTest.java");
        testFileTextEqual(inputFile);
    }


    @Test
    public void MethodTest()
    {
        Path inputFile = Paths.get("MethodTest.java");
        testFileTextEqual(inputFile);
    }

    @Test
    public void FieldTest()
    {
        Path inputFile = Paths.get("FieldTest.java");
        testFileTextEqual(inputFile);
    }

    @Test
    public void LoopTest()
    {
        Path inputFile = Paths.get("LoopTest.java");
        testFileTextEqual(inputFile);
    }

    @Test
    public void TryTest()
    {
        Path inputFile = Paths.get("TryTest.java");
        testFileTextEqual(inputFile);
    }


    // note: default cases should be written in lowerCase
    @Test
    public void SwitchTest()
    {
        Path inputFile = Paths.get("SwitchTest.java");
        testFileTextEqual(inputFile);
    }



    // note: the enums are at the end because the parser places them there
    @Test
    public void CanvasTest()
    {
        Path inputFile = Paths.get("combined","Canvas.java");
        testFileTextEqual(inputFile);
    }



    private void testFileTextEqual (Path inputFile)
    {
        System.out.println("ViewerTest testing: "+inputFile);
        Set<Node.Op> nodes = reader.read(base, new Path[]{inputFile});
        Set<Node> outputNodes = nodes.stream().map(Node.class::cast).collect(Collectors.toSet());
        Node rootNode = outputNodes.stream().findFirst().get().getRoot();
        Assertions.assertNotNull(rootNode);
        viewer.showTree(rootNode);
        ObservableList<JavaBlockInterface> items = viewer.listView.getItems();

        StringBuilder sb = new StringBuilder();
        for (JavaBlockInterface item : items) {
            collectTextFromJavaBlock(item, sb);
        }

        String fileText = fileFilteredText(inputFile);
        Assertions.assertEquals(fileText, sb.toString());
    }

    private void collectTextFromJavaBlock(JavaBlockInterface item,StringBuilder sb)
    {
        item.getCellContent().getChildren().forEach(child -> {
            recursiveCollectTextFromJavaBlock(child,sb);
        });
        }

    private void recursiveCollectTextFromJavaBlock(javafx.scene.Node node, StringBuilder sb)
    {
        if (node instanceof Label label) {
            for (char c : label.getText().toCharArray()) {
                if (! Character.isWhitespace(c)) sb.append(c);
            }
        }else if (node instanceof Pane pane) {
            for (javafx.scene.Node child : pane.getChildren()) {
                recursiveCollectTextFromJavaBlock(child, sb);
            }
        }
    }

    private String fileFilteredText(Path filePath) {
        StringBuilder fileTextBuilder  = new StringBuilder();
        try{
            Reader reader = new FileReader(base.resolve(filePath).toFile());
            while (reader.ready()) {
                char c = (char) reader.read();
                if (! Character.isWhitespace(c)) fileTextBuilder.append(c);
            }
        } catch (IOException e) {
            Assertions.fail(" was unable to read a file for a test : " + e);
        }
        String returnString = fileTextBuilder.toString();
        Assertions.assertFalse(returnString.isEmpty());
        return returnString;
    }

    // https://stackoverflow.com/questions/45109876/toolkit-not-initialized-exception-when-unit-testing-an-javafx-application
    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }
}
