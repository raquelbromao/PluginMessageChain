package plugin.raquel.examples.helloworld;

//import plugin.raquel.examples.helloworld.ExpressionInvoke;
import plugin.raquel.examples.helloworld.Results;

//import java.util.regex.Pattern;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

//import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
//import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
/*import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;*/

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Text;

/**
 * HelloWorldAction is a simple example of using an action set to extend the
 * Eclipse Workbench with a menu and toolbar action that prints the "Hello
 * World" message.
 */
public class HelloWorldAction implements IWorkbenchWindowActionDelegate {
	IWorkbenchWindow activeWindow = null;
	public Shell shlMessageChain;
	//private static Text results;
	IProject projectSelection;
	IPackageFragment[] packagesSelection;

	/**
	 * Lista os projetos da Workspace em utilização
	 */
	public IProject[] getAllProjects() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return projects;
	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 *
	 * @param unit
	 * @return
	 */
	/*private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}*/

	/**
	 * Run the action. Display the plugin
	 */
	public void run(IAction proxyAction) {
		// proxyAction has UI information from manifest file (ignored)
		shlMessageChain = new Shell();
		shlMessageChain.setSize(547, 300);
		shlMessageChain.setText("Message Chain Plugin");
		shlMessageChain.setLayout(null);

		Label lbl = new Label(shlMessageChain, SWT.NONE);
		lbl.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lbl.setBounds(25, 10, 394, 15);
		lbl.setText("Message Chain: all methods in workspace!");

		Combo comboProjects = new Combo(shlMessageChain, SWT.NONE);
		comboProjects.setBounds(25, 27, 425, 23);

		// Gets all projects from workspace
		IProject[] projects = getAllProjects();
		for (int i = 0; i < projects.length; i++) {
			comboProjects.add(projects[i].getName());
		}

		comboProjects.select(0);

		Button btnApplyProjects = new Button(shlMessageChain, SWT.NONE);
		btnApplyProjects.setSelection(true);
		btnApplyProjects.setBounds(456, 25, 75, 25);
		btnApplyProjects.setText("Apply");

		Button btnCancel = new Button(shlMessageChain, SWT.NONE);
		btnCancel.setBounds(456, 50, 75, 25);
		btnCancel.setText("Cancel");

		/*Button btnClear = new Button(shlMessageChain, SWT.NONE);
		btnClear.setBounds(456, 124, 75, 25);
		btnClear.setText("Clear");*/
		
		/*Button btnTeste = new Button(shlMessageChain, SWT.NONE);
		btnTeste.setText("Teste");
		btnTeste.setBounds(456, 155, 75, 25);*/

		btnApplyProjects.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					// remove todas as classes do projeto escolhido anteriormente
					//comboClasses.removeAll();
					// LIMPA A JANELA DOS RESULTADOS QUANDO SELECIONADO UM NOVO PROJETO
					//results.setText("");

					// Acha a raiz da workspace para criar/carregar o IProject selecionado pelo usuário
					String nameProject = comboProjects.getItem(comboProjects.getSelectionIndex());
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IWorkspaceRoot root = workspace.getRoot();

					// Pega a raiz do projeto selecionado pelo usuário
					projectSelection = root.getProject(nameProject);
					//results.append("## NAME OF PROJECT: " + projectSelection.getName() + "\n");
					//results.append("## PATH OF PROJECT: " + projectSelection.getFullPath() + "\n");
					projectSelection.open(null);

					// Gera a lista de todas as classes do projeto selecionado
					// com o tipo IPackageFragment que obtenho todas as classes de um projeto
					// IProject -> IPackageFragment -> ICompilationUnit -> arq.java
					packagesSelection = JavaCore.create(projectSelection).getPackageFragments();

					Results.main(null,packagesSelection);
					//TesteDesign.main(null, packagesSelection);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		/*btnApplyClass.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					// LIMPA A JANELA DOS RESULTADOS QUANDO SELECIONADO UMA NOVA CLASSE
					results.setText("");
					String nameClass = comboClasses.getItem(comboClasses.getSelectionIndex());

					for (IPackageFragment mypackage : packagesSelection) {
						for (final ICompilationUnit compilationUnit : mypackage.getCompilationUnits()) {
							String aux = compilationUnit.getElementName();
							if (aux.equals(nameClass)) {
								analyseClass(compilationUnit);
							}
						}
					}

				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});*/

		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shlMessageChain.close();
			}
		});

		shlMessageChain.pack();
		shlMessageChain.open();
		/*btnClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				results.setText("");
			}
		});*/
		
		/*btnTeste.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {							      
				Results.main(null,packagesSelection);
			}
		});*/
	}

	// IActionDelegate method
	public void selectionChanged(IAction proxyAction, ISelection selection) {
		// do nothing, action is not dependent on the selection
	}

	// IWorkbenchWindowActionDelegate method
	public void init(IWorkbenchWindow window) {
		activeWindow = window;
	}

	// IWorkbenchWindowActionDelegate method
	public void dispose() {
		// nothing to do
	}
}