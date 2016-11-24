package plugin.raquel.examples.helloworld;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VariableDeclarationStatementVisitor extends ASTVisitor {
	List<VariableDeclarationStatement> methods = new ArrayList<VariableDeclarationStatement>();

	public boolean visit(VariableDeclarationStatement node) {
		methods.add(node);
		return super.visit(node);
	}

	public List<VariableDeclarationStatement> getExpression() {
		return methods;
	}
}
