package plugin.raquel.examples.helloworld;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;

public class IfStatementVisitor extends ASTVisitor {
	List<IfStatement> methods = new ArrayList<IfStatement>();

	public boolean visit(IfStatement node) {
		methods.add(node);
		return super.visit(node);
	}

	public List<IfStatement> getExpression() {
		return methods;
	}
}
