package Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pojo.AccessToken;
import pojo.Image;
import pojo.ImageMessage;
import pojo.News;
import pojo.NewsMessage;
import pojo.TextMessage;

import com.thoughtworks.xstream.XStream;

/**
 * 说明：
 * 
 * @author EZ.TangChao
 * @since 2016-8-5
 * @version 0.1
 */
public class MessageUtil {

	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_NEWS = "news";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_VOICE = "voice";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_LINK = "link";
	public static final String MESSAGE_LOCATION = "location";
	public static final String MESSAGE_EVNET = "event";
	public static final String MESSAGE_SUBSCRIBE = "subscribe";
	public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static final String MESSAGE_CLICK = "CLICK";
	public static final String MESSAGE_VIEW = "VIEW";
	public static final String MESSAGE_SCANCODE = "scancode_push";

	/**
	 * xml转为map
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> xmlToMap(HttpServletRequest request)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();

		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);

		Element root = doc.getRootElement();

		List<Element> list = root.elements();

		for (Element element : list) {
			map.put(element.getName(), element.getText());
		}
		ins.close();
		return map;
	}

	/**
	 * 消息转换为xml
	 * 
	 * @param textMessage
	 * @return
	 */
	public static String textMessageToXml(TextMessage textMessage) {
		XStream xStream = new XStream();
		xStream.alias("xml", textMessage.getClass());
		return xStream.toXML(textMessage);
	}

	public static String initText(String toUserName, String fromUserName,
			String content) {
		TextMessage text = new TextMessage();
		text.setContent(content);
		text.setCreateTime(new Date().getTime());
		text.setFromUserName(toUserName);
		text.setToUserName(fromUserName);
		text.setMsgType(MESSAGE_TEXT);
		return MessageUtil.textMessageToXml(text);
	}

	public static String menuText() {
		StringBuffer sb = new StringBuffer();
		sb.append("欢迎你的关注请输入以下数字来选择你要使用的功能:\n\n");
		sb.append("1.公司简介\n");
		sb.append("2.招聘信息\n");
		sb.append("3.公司官网\n");
		return sb.toString();
	}

	public static String firstMenu() {
		StringBuffer sb = new StringBuffer();
		sb.append("大连亚迅科技有限公司，是一家软件开发，人才派遣及提供专业IT咨询服务、"
				+ "教育咨询服务的公司；同时在软件外包解决方案和人力资源解决方案方面具备强大的实施能力。"
				+ "亚迅公司集中了众多软件行业优秀的技术人才；并在行业咨询和教育咨询方面拥有多名领域内的资深顾问；"
				+ "亚迅的团队成员绝大部分都具备在500强企业和国内大型IT企业的背景经历；"
				+ "亚迅以其高素质的咨询团队、高水平的技术力量和成熟的服务模式为众多企业客户和基础人才提供高质量技术与"
				+ "人才解决方案。尤其在IT咨询服务和教育咨询服务领域内，"
				+ "亚迅科技有限公司服务于全国多家著名的软件外包企业和大型教育机构/集团。"
				+ "亚迅公司坚持着为企业客户和个人客户创造最高价值的服务理念，以成为国内领先的软件外包，"
				+ "人才派遣，IT咨询服务提供商为愿景不断的发展前行。");
		return sb.toString();
	}

	public static String secondMenu() {
		StringBuffer sb = new StringBuffer();
		sb.append("IT服务外包与解决方案亚迅科技为企业客户提供端对端的行业咨询服务；分析客户所在的行业"
				+ "领域、分析市场和细化市场，同时针对客户公司目前的员工情况、市场情况、技术情况综合分析"
				+ "后为客户提供最终的对应解决方案和实施方案。亚迅科技具有行业内资深的业务顾问和规划"
				+ "分析人员，有能力根据调研分析后的结果为客户提供可行的运营方案和业务规划。在技术方"
				+ "面亚迅科技同样具备行业内优秀的技术人员，可以为客户提供优质的技术基础咨询服务；亚迅科技在汽车制造、"
				+ "P2P信贷、O2C电商、保险、OA、制药、教育等方面具备扎实的技术能力，我们有信心为终"
				+ "端客户提供完善的咨询服务与外包服务。IT咨询服务&服务外包☆    基于行业的咨询与服务银行、"
				+ "保险、证劵、物流、交通、制药、通信、教育☆    基于解决方案的咨询与服务供应链管理（SCM）"
				+ "、客户关系管理（CRM）、企业资源计划（ERP）、电子商务☆    "
				+ "IT基础设施和数据安全咨询与服务企业应用管理、基础架构管理、服务端平台、数据中心服务、"
				+ "桌面及网络服务☆    应用系统开发及维护服务企业应用、测试与本地化");
		return sb.toString();
	}

	public static String unsubscribe() {
		StringBuffer sb = new StringBuffer();
		sb.append("期待你下次再次关注我们公司的公众号");
		return sb.toString();
	}

	/**
	 * 说明： 图文消息转xml
	 * 
	 * @param newsMessage
	 * @return
	 */
	public static String newMessageToXml(NewsMessage newsMessage) {

		XStream xStream = new XStream();
		xStream.alias("xml", newsMessage.getClass());
		xStream.alias("item", new News().getClass());

		return xStream.toXML(newsMessage);
	}
	
	
	
	/**
	 * 说明： 图片消息转换为xml
	 * 
	 * @param newsMessage
	 * @return
	 */
	public static String imageMessageToXml(ImageMessage imageMessage) {

		XStream xStream = new XStream();
		xStream.alias("xml", imageMessage.getClass());
		return xStream.toXML(imageMessage);
	}

	/**
	 * 说明： 图文消息组装
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String initNewsMessage(String toUserName, String fromUserName) {

		String message = null;
		List<News> newList = new ArrayList<News>();
		NewsMessage newsmessage = new NewsMessage();

		News news = new News();
		news.setTitle("公司官网");
		news.setDescription("大连亚迅科技有限公司，是一家软件开发，人才派遣及提供专业IT咨询服务、"
				+ "教育咨询服务的公司；同时在软件外包解决方案和人力资源解决方案方面具备强大的实施能力。");
		news.setPicUrl("http://chijiaxin.com.ngrok.cc/WeiXinn/image/pic01.jpg");
		news.setUrl("http://yaxun.11.userhostting.com/");

		newList.add(news);

		newsmessage.setArticles(newList);
		newsmessage.setArticleCount(newList.size());
		newsmessage.setCreateTime(new Date().getTime());
		newsmessage.setFromUserName(toUserName);
		newsmessage.setMsgType(MESSAGE_NEWS);
		newsmessage.setToUserName(fromUserName);

		message = newMessageToXml(newsmessage);
		return message;
	}

	/**
	 * 说明： 图片消息回复
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String initImageMessage(String toUserName, String fromUserName) {

		try {
			AccessToken at = new AccessToken();
			at = WeiXinUntil.getAccessToken();
			String path = "C:\\Users\\Administrator.PC-20160708SVDR\\Desktop\\11.jpg";
			String mediaId = WeiXinUntil.upload(path, at.getAccess_token(),
					"image");
			String message = null;
			Image image = new Image();
			image.setMediaId(mediaId);
			ImageMessage im = new ImageMessage();
			im.setFromUserName(toUserName);
			im.setToUserName(fromUserName);
			im.setMsgType(MESSAGE_IMAGE);
			im.setCreateTime(new Date().getTime());
			im.setImage(image);
			message = imageMessageToXml(im);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
