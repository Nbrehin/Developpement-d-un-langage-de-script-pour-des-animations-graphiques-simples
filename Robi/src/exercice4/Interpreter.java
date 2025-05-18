package exercice4;

import stree.parser.SNode;

public class Interpreter {
	public void compute(Environment environment, SNode next) {
		String receiverName = next.get(0).contents();
		Reference receiver = environment.getReferenceByName(receiverName);
		receiver.run(next);
	}
}
