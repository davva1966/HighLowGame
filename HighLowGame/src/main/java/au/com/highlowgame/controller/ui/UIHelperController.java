package au.com.highlowgame.controller.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
public class UIHelperController {

	protected class ComboData {
		public Object id;
		public String text;
		public String extraText;

		public ComboData(Object id, String text) {
			this(id, text, null);
		}

		public ComboData(Object id, String text, String extraText) {
			this.id = id;
			this.text = text;
			this.extraText = extraText;
		}

	}

	protected class ComboDataHolder {
		public String identifier;
		public String label;
		public long totalCount;
		public List<ComboData> items = new ArrayList<UIHelperController.ComboData>();

		public ComboDataHolder(long totalCount) {
			this.totalCount = totalCount;
		}

		public ComboDataHolder(String identifier, String label) {
			this.identifier = identifier;
			this.label = label;
		}

		public void addData(ComboData data) {
			this.items.add(data);
		}

	}

}
