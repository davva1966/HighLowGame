package au.com.highlowgame.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import au.com.highlowgame.model.Player;
import au.com.highlowgame.model.validate.PlayerValidator;
import au.com.highlowgame.service.Image;
import au.com.highlowgame.service.ImageService;
import au.com.highlowgame.service.TranslationService;
import au.com.highlowgame.user.UserContextService;
import au.com.highlowgame.util.ApplicationContextProvider;
import au.com.highlowgame.util.RedirectHelper;

@RequestMapping("/players")
@Controller
public class PlayerController {

	@Autowired
	TranslationService translationService;

	@Autowired
	ImageService imageService;

	@Autowired
	RedirectHelper redirectHelper;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		PlayerValidator validator = ApplicationContextProvider.getApplicationContext().getBean(PlayerValidator.class);
		validator.setEmbeddedValidator(binder.getValidator());
		binder.setValidator(validator);
	}

	@RequestMapping(params = "form", produces = "text/html")
	public String createForm(Model uiModel) {
		Player player = new Player();
		populateEditForm(uiModel, player);
		return "players/create";
	}

	@RequestMapping(value = "/myinfo", method = RequestMethod.POST, produces = "text/html")
	public String myinfoPost(@Valid Player player, BindingResult bindingResult, Model uiModel, @RequestParam(value = "imageContent", required = false) MultipartFile imageContent, HttpServletRequest request) {
		return myinfo(player, bindingResult, uiModel, imageContent, request);
	}

	@RequestMapping(value = "/myinfo", method = RequestMethod.PUT, produces = "text/html")
	public String myinfo(@Valid Player player, BindingResult bindingResult, Model uiModel, @RequestParam(value = "imageContent", required = false) MultipartFile imageContent, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, player);
			return "players/myinfo";
		}

		if (imageContent != null && imageContent.getSize() > 0)
			updateAvatarFromContent(player, imageContent);

		boolean logout = false;
		Player oldPlayer = Player.find(player.getId());
		if (oldPlayer != null) {
			if (oldPlayer.getEmail().equalsIgnoreCase(player.getEmail()) == false)
				logout = true;
		}

		uiModel.asMap().clear();
		player.merge();

		if (logout) {
			return redirectHelper.redirectTo("/resources/j_spring_security_logout");
		} else {
			return "index";
		}
	}

	@RequestMapping(value = "/myinfo", params = "form", produces = "text/html")
	public String myinfoForm(Model uiModel) {
		populateEditForm(uiModel, Player.find(UserContextService.getCurrentPlayer().getId()));
		return "players/myinfo";
	}

	void populateEditForm(Model uiModel, Player player) {
		uiModel.addAttribute("player", player);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST, produces = "text/html")
	public String updatePost(@Valid Player player, BindingResult bindingResult, Model uiModel, @RequestParam(value = "imageContent", required = false) MultipartFile imageContent, HttpServletRequest request) {
		return update(player, bindingResult, uiModel, imageContent, request);
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@Valid Player player, BindingResult bindingResult, Model uiModel, @RequestParam(value = "imageContent", required = false) MultipartFile imageContent, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, player);
			return "players/update";
		}

		if (imageContent != null && imageContent.getSize() > 0)
			updateAvatarFromContent(player, imageContent);

		uiModel.asMap().clear();
		player.merge();
		return "redirect:/players/" + encodeUrlPathSegment(player.getId().toString(), request);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid Player player, BindingResult bindingResult, Model uiModel, @RequestParam(value = "imageContent", required = false) MultipartFile imageContent, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, player);
			return "players/create";
		}

		if (imageContent != null && imageContent.getSize() > 0)
			updateAvatarFromContent(player, imageContent);

		uiModel.asMap().clear();
		player.persist();
		return "redirect:/players/" + encodeUrlPathSegment(player.getId().toString(), request);
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		uiModel.addAttribute("player", Player.find(id));
		uiModel.addAttribute("itemId", id);
		return "players/show";
	}

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 50 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("players", Player.findPlayerEntries(firstResult, sizeNo));
			float nrOfPages = (float) Player.countPlayers() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("players", Player.findAllPlayers());
		}
		return "players/list";
	}

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") String id, Model uiModel) {
		populateEditForm(uiModel, Player.find(id));
		return "players/update";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest request) {
		Player player = Player.find(id);
		if (player != null) {

			if (UserContextService.getCurrentPlayer() != null) {
				if (player.equals(UserContextService.getCurrentPlayer())) {
					uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_player_cannotdeletecurrentplayer"));
					return show(id, uiModel, request);
				}
			}

			player.remove();
		}
		uiModel.asMap().clear();
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "50" : size.toString());
		return "redirect:/players";
	}

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
		String enc = request.getCharacterEncoding();
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		return UriUtils.encodePathSegment(pathSegment, enc);
	}

	protected void updateAvatarFromContent(Player player, MultipartFile avatarContent) {
		if (avatarContent != null && avatarContent.getSize() > 0) {

			Image image = new Image();
			try {
				image.setContent(avatarContent.getBytes());
			} catch (Exception e) {
			}
			image.setContentType(avatarContent.getContentType());
			image.setSize(avatarContent.getSize());

			if (avatarContent.getSize() > 51200l)
				imageService.resize(image, 300, 300);

			player.setAvatarContent(image.getContent());
			player.setAvatarContentType(image.getContentType());
			player.setAvatarContentSize(image.getSize());

		}
	}

	@RequestMapping(value = "showAvatar/{id}", method = RequestMethod.GET)
	public String showAvatar(@PathVariable("id") String id, @RequestParam(value = "thumbnail", required = false) Boolean thumbnail, @RequestParam(value = "mediumthumbnail", required = false) Boolean mediumthumbnail, @RequestParam(value = "fullsize", required = false) Boolean fullsize, HttpServletResponse response,
			Model model) {
		Player player = Player.find(id);
		if (player == null)
			return null;

		Image avatar = player.getAvatarImage();
		if (avatar == null)
			return null;

		try {
			response.setHeader("Content-Disposition", "inline;filename=\"Avatar\"");
			OutputStream out = response.getOutputStream();

			byte[] imageContent;
			if (thumbnail != null && thumbnail) {
				imageContent = avatar.getThumbnailContent();
			} else if (mediumthumbnail != null && mediumthumbnail) {
				imageContent = avatar.getMediumThumbnailContent();
			} else if (fullsize == null || fullsize == false) {
				imageContent = avatar.getMediumSizeContent();
			} else {
				imageContent = avatar.getContent();
			}

			response.setContentType(avatar.getContentType());
			out.write(imageContent);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
