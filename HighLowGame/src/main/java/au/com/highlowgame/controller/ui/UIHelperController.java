package au.com.highlowgame.controller.ui;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.highlowgame.model.Player;
import au.com.highlowgame.util.SSUtil;
import au.com.highlowgames.model.util.EntitySearchDescriptor;
import flexjson.JSONSerializer;

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
	
	protected class ImageComboData extends ComboData {
		public String imageUrl;

		public ImageComboData(Object id, String text, String extraText, String imageUrl) {
			super(id, text, extraText);
			this.imageUrl = imageUrl;
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

	@RequestMapping(value = "/players", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<String> getPlayers(@RequestParam(value = "query", required = false) String query, @RequestParam(value = "page", required = false) Integer page, HttpServletRequest request) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		int pageSize = 30;
		final int firstResult = page == null ? 0 : (page.intValue() - 1) * pageSize;

		EntitySearchDescriptor searchDescriptor = new EntitySearchDescriptor();

		int searchTermIndex = 0;
		StringBuilder sb = new StringBuilder();

		if (SSUtil.notEmpty(query, true)) {
			String[] searchTerms = SSUtil.formatSeparatedToArray(query, " ", true);

			sb.append("(");
			for (String searchTerm : searchTerms) {
				searchTermIndex++;
				searchDescriptor.addSearchTerm("%" + searchTerm + "%");
				if (searchTermIndex > 1)
					sb.append(" AND ");
				sb.append("(");
				searchDescriptor.addSearch(sb, "name", "like", searchTermIndex);
				sb.append(" OR ");
				searchDescriptor.addSearch(sb, "email", "like", searchTermIndex);
				sb.append(")");
			}
			sb.append(")");

			sb.append(" AND ");
		}

		searchTermIndex++;
		searchDescriptor.addSearchTerm(Boolean.FALSE);
		searchDescriptor.addSearch(sb, "disabled", " = ", searchTermIndex);

		searchDescriptor.setQueryString(sb.toString());

		long totalCount = Player.countPlayers(searchDescriptor);
		List<Player> players = Player.findPlayerEntries(firstResult, 30, searchDescriptor);
		
		String context = request.getContextPath();

		ComboDataHolder dh = new ComboDataHolder(totalCount);
		for (Player player : players) {
			dh.addData(new ImageComboData(player.getId(), player.getName(), player.getEmail(), context + player.getAvatarThumbnailUrl()));

		}
		String json = new JSONSerializer().exclude("*.class").include("items").serialize(dh);
		return new ResponseEntity<String>(json, headers, HttpStatus.OK);
	}

}
