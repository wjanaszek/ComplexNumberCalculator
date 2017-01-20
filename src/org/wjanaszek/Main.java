package org.wjanaszek;

import org.wjanaszek.model.*;
import org.wjanaszek.view.*;
import org.wjanaszek.controller.*;

public class Main {

	public static void main(String[] args) {
		Model model = new Model();
		View view = new View();
		Controller ctr = new Controller(view, model);
		ctr.start();
	}

}
