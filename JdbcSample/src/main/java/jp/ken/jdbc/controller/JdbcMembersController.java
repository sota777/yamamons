package jp.ken.jdbc.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.ken.jdbc.dao.MembersDaoImple;
import jp.ken.jdbc.entity.Members;
import jp.ken.jdbc.groups.GroupOrder;
import jp.ken.jdbc.model.MembersModel;
import jp.ken.jdbc.model.MembersSearchModel;

@Controller
public class JdbcMembersController {
	@Autowired
	private MembersDaoImple membersDao;

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String toSearch(Model model) {
		model.addAttribute("membersSearchModel", new MembersSearchModel());
		model.addAttribute("headline", "会員検索");
		return "membersSearch";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String searchMembers(@ModelAttribute MembersSearchModel membersSearchModel, Model model) {
		//IDと名前が未入力か判定
		boolean idIsEmpty = membersSearchModel.getId().isEmpty();
		boolean nameIsEmpty = membersSearchModel.getName().isEmpty();

		//IDと氏名が未入力の場合
		if (idIsEmpty && nameIsEmpty) {
			//全件検索
			List<Members> membersList = membersDao.getList();
			model.addAttribute("membersList", membersList);
			System.out.println(membersList.size());

			//IDだけが入力された場合
		} else if (!idIsEmpty && nameIsEmpty) {
			try {
				//ID検索
				Integer id = new Integer(membersSearchModel.getId());
				Members members = membersDao.getMembersById(id);

				if (members == null) {
					model.addAttribute("message", "該当データはありません");
				} else {
					List<Members> membersList = new ArrayList<Members>();
					membersList.add(members);
					model.addAttribute("membersList", membersList);
				}
			} catch (NumberFormatException e) {
				model.addAttribute("message", "IDが不正です");
			}

			//氏名だけが入力された場合
		} else if (idIsEmpty && !nameIsEmpty) {
			//氏名検索(あいまい検索)
			List<Members> membersList = membersDao.getListByName(membersSearchModel.getName());

			if (membersList.isEmpty()) {
				model.addAttribute("message", "該当データはありません");
			} else {
				model.addAttribute("membersList", membersList);
			}

			//IDと氏名ともに入力された場合
		} else {
			model.addAttribute("message", "IDまたは氏名のいずれかを入力してください");
		}

		model.addAttribute("headline", "会員検索");
		return "membersSearch";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String toRegistration(Model model) {
		model.addAttribute("membersModel", new MembersModel());
		model.addAttribute("headline", "会員登録");
		return "memberRegistration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registMembers(Model model, @Validated(GroupOrder.class) @ModelAttribute MembersModel membersModel,
			BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("headline", "会員登録");
			return "memberRegistration";
		}

		Members members = new Members();
		members.setName(membersModel.getName());
		members.setEmail(membersModel.getEmail());
		members.setPhoneNumber(membersModel.getPhoneNumber());
		members.setBirthday(Members.parseDate(membersModel.getBirthday()));

		int numberRow = membersDao.insert(members);
		if (numberRow == 0) {
			model.addAttribute("message", "登録に失敗しました");
			model.addAttribute("headline", "会員登録");
			return "memberRegistration";
		}

		return "redirect:/complete";
	}

	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public String toComlete(Model model) {
		model.addAttribute("headline", "会員登録完了");
		return "memberRegistrationComplete";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String toDelete(@ModelAttribute MembersSearchModel searchModel, Model model) {
		model.addAttribute("headline", "会員削除ページ");
		model.addAttribute("confirmMessage", "ID:" + searchModel.getId() + "を削除しますか");
		return "membersDelete";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String executeDelete(Model model, @ModelAttribute MembersSearchModel searchModel, BindingResult result) {
		int numberRow = membersDao.delete(searchModel);
		if (numberRow == 0) {
			model.addAttribute("message", "削除に失敗しました");
			model.addAttribute("headline", "会員検索");
			return "membersSearch";
		}
		model.addAttribute("completeMessage", "削除しました。");
		return "membersDelete";
	}


	//変更ボタンをhrefで"search/update?id="で設定すればGET通信で送信パラメータを入れて送信してくれる
	//(Model model, @RequestParam("id") String id)
	//リクエストパラメータのidをStringのidに入れてくれる
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String toUpdate(@ModelAttribute MembersSearchModel searchModel,MembersModel membersModel, Model model) {
		Integer id = new Integer(searchModel.getId());
		System.out.println("変更するID:"+searchModel.getId());

		Members members = membersDao.getMembersById(id);
		List<Members> membersList = new ArrayList<Members>();
		membersList.add(members);

		membersModel.setName(members.getName());
		membersModel.setEmail(members.getEmail());
		membersModel.setPhoneNumber(members.getPhoneNumber());
		membersModel.setBirthday(String.valueOf(members.getBirthday()).replace("-","/"));
		//model.addAttribute("id",members.getId());
		searchModel.setId(String.valueOf(members.getId()));

		model.addAttribute("headline", "会員情報変更");
		return "membersUpdate";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String executeUpdate(@Validated(GroupOrder.class) @ModelAttribute MembersModel membersModel,BindingResult result,MembersSearchModel searchModel, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("headline", "会員情報変更");
			return "membersUpdate";
		}
		System.out.println("post通信");
		Members members = new Members();
		members.setName(membersModel.getName());
		members.setEmail(membersModel.getEmail());
		members.setPhoneNumber(membersModel.getPhoneNumber());
		members.setBirthday(Members.parseDate(membersModel.getBirthday()));
		members.setId(Integer.parseInt(searchModel.getId()));

		int numberRow = membersDao.update(members);
		if (numberRow == 0) {
			model.addAttribute("message", "変更に失敗しました");
			model.addAttribute("headline", "会員情報変更");
			return "membersUpdate";
		}

		return "redirect:/complete";

	}

}
