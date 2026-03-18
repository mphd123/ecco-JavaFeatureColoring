package at.jku.cdl.ecco.adapter.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import at.jku.cdl.ecco.adapter.JavaASTReader;
import at.jku.cdl.ecco.adapter.JavaASTWriter;
import at.jku.isse.ecco.storage.ser.dao.SerEntityFactory;
import at.jku.isse.ecco.tree.Node;

public class JavaASTTest {
	
	private JavaASTWriter writer;
	private JavaASTReader reader;
	private Path base = Paths.get("D:\\workspace_argouml\\ArgoUMLSPLBenchmark\\scenarios\\ScenarioAllVariants\\variants\\00005.config\\src\\");
	private Path[] input = {Paths.get("org\\argouml\\application\\Main.java")};
	
	public static void main(String[] args) {
		new JavaASTTest().test();
	}
	
	private void test() {
		Set<Node.Op> nodes = reader.read(base, input);
		Set<Node> outputNodes = nodes.stream().map(Node.class::cast).collect(Collectors.toSet());
//		Path[] output = writer.write(base, outputNodes);
//		output.clone();
		System.out.println(outputNodes);
		
	}

	public JavaASTTest() {
		writer = new JavaASTWriter();
		reader = new JavaASTReader(new SerEntityFactory());
		// was reader = new JavaASTReader(new MemEntityFactory());
	}

}
