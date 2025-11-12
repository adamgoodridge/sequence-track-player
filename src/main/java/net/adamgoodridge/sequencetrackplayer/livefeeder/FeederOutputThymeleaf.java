package net.adamgoodridge.sequencetrackplayer.livefeeder;


import lombok.*;

@Data
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
}
