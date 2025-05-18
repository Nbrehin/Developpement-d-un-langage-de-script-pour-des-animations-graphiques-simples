package ihm;

import stree.parser.SNode;

public class Interpreter {
	public void compute(Environment environment, SNode next) {
		// quel est le nom du receiver
		String receiverName = next.get(0).contents();
		// quel est le receiver
		Reference receiver = environment.getReferenceByName(receiverName);
		// demande au receiver d'executer la s-expression compilee
		receiver.run(next);
	}
}
