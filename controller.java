仮に平野編集します。


//とりあえずコピペですがサンプルコード入れます
package jp.ken.calculation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jp.ken.calculation.exception.NegativeException;
import jp.ken.calculation.model.CalculationModel;

@Controller
public class CalculationController {

	@RequestMapping(value="/sample",method=RequestMethod.GET)
	public String toCalculation(Model model) {//以下Viewで使う部品の登録
		model.addAttribute("message","整数を入力してください");
		model.addAttribute("calculationModel",new CalculationModel());
		return "Calculation";
	}

	@RequestMapping(value="/sample",method=RequestMethod.POST)
	public String calculate(@ModelAttribute CalculationModel calculationModel,Model model)
			throws NumberFormatException,NegativeException,ArithmeticException{//3つの例外を投げる
		
		//NumberFormatExceptionの可能性あり（変換処理があるため）
		int value1= Integer.parseInt(calculationModel.getValue1());//変換処理
		int value2= Integer.parseInt(calculationModel.getValue2());//変換処理
		int operator= Integer.parseInt(calculationModel.getOperator());//変換処理

		//NegativeExceptionの可能性あり
		if(value1<0 || value2<0) {
			throw new NegativeException();//独自例外を投げる際はthrowを使う
		}

		int answer;//計算処理
		switch (operator) {//operator:演算子
		case 1:
			answer=value1+value2;
			break;
		case 2:
			answer=value1-value2;
			break;
		case 3:
			answer=value1*value2;
			break;
		case 4://ここで0除算であるArismeticExceptionの可能性あり
			answer=value1/value2;
			break;
		default:
			answer=0;
		}

		model.addAttribute("answer",answer);//Modelに部品登録
		model.addAttribute("message","計算結果");
		return "Calculation";
	}
		//例外処理の結果を実行するメソッド
		//引数で渡されたメッセージを表示するように作られたエラーページのModelAndViewを表示
	
	public ModelAndView toError(String message) {//エラー処理用の部品用意
		ModelAndView model=new ModelAndView("error");//ModelAndViewのコンストラクタの引数：JSP（文字列)
		model.addObject("error", message);
		model.addObject("url", "sample");
		return model;//ModelAndView型のインスタンスをreturnする！！（ModelAndView型には
	}
		//以下例外処理（例外処理は、別コントローラーでは動かない。※コントローラー単位で動く！！！）
	@ExceptionHandler(NumberFormatException.class)//NumberFormatExceptionが発生時はこのメソッド実行
	public ModelAndView handleException(NumberFormatException e) {
		return toError("未入力もしくは整数以外の値では計算できません");//実行結果えをreturnで返す
	}

	@ExceptionHandler(NegativeException.class)//独自例外はこのメソッド実行
	public ModelAndView handleException(NegativeException e) {
		return toError("マイナスの整数は計算対象外です");//実行結果えをreturnで返す
	}
	@ExceptionHandler(ArithmeticException.class)//割算で0が出たら、0で割れないのでこのメソッド実行
	public ModelAndView handleException(ArithmeticException e) {
		return toError("0で割り算することはできません");//実行結果えをreturnで返す
	}
}
