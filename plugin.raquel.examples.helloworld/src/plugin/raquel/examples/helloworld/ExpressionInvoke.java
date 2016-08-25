package plugin.raquel.examples.helloworld;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ExpressionStatement;

public class ExpressionInvoke extends ASTVisitor {

	List<ExpressionStatement> methods = new ArrayList<ExpressionStatement>();

	@Override
	public boolean visit(ExpressionStatement node) {
		methods.add(node);
		return super.visit(node);
	}

	public List<ExpressionStatement> getMethods() {
		return methods;
	}

	/*public String ArrayMetInvoc() {
		// Cria uma string para armazenamento de todos os métodos invocados
		String[] metinvoke = null;
		int i = 0;

		// Guarda o método invocado e o imprime na tela p/ teste
		while (methods != null) {
			metinvoke[i] = methods.toString();
			i++;
			System.out.println("Método Invocado [" + i + "]: " + metinvoke[i]);
		}
		return null;
	}*/
}