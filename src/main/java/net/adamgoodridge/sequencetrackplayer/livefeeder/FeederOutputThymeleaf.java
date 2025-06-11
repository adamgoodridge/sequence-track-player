
package net.adamgoodridge.sequencetrackplayer.livefeeder;


public class FeederOutputThymeleaf{
	private FeederOutput feederOutput;
	private String cssClass;
	private String html;
	public FeederOutputThymeleaf(FeederOutput feederOutput) {
		this.feederOutput = feederOutput;

		String classSubName = feederOutput.isRecent() ? "success": "danger";
		html = "<tr class=\"table-"+ classSubName +"\">";

		html += "<td>" + feederOutput.getId() + "</td>";
		html += "<td><a href=\"" + feederOutput.getUrl() +"\" target=\"_blank\" a> " + feederOutput.getName() + "</a></td>";
		html += "<td>"+ feederOutput.getLastUpdateTime() + "</td>";
		html += "</tr>";
		
	}
	
	public void setFeederOutput(FeederOutput feederOutput) {
		this.feederOutput = feederOutput;
	}
	
	public String getCssClass() {
		return cssClass;
	}
	
	public void setCssClass() {
		this.cssClass = feederOutput.isRecent() ? "table-success" : "tale-danger";
	}
	
	public String getHtml() {
		return html;
	}
	
	public void setHtml(String html) {
		this.html = html;
	}
}
