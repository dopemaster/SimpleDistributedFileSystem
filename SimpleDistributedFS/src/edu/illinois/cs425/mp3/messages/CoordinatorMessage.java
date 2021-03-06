package edu.illinois.cs425.mp3.messages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.FileIndexer;
import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class CoordinatorMessage extends GenericMessage {
	public MemberNode masterNode;
	public CoordinatorMessage(MemberNode masterNode) {
		this.masterNode = masterNode;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.getLogger().info("The elected new master is: " + masterNode.getHostAddress());
		process.setMaster(masterNode);		
		if(process.getNode().equals(masterNode)) {
			for(MemberNode node: process.getGlobalList()) {
				List<FileIdentifier> fileIdentifiers = (List<FileIdentifier>) process.getTcpServer().sendRequestMessage(new FileIndexerRequestMessage(null), node.getHostAddress(), process.TCP_SERVER_PORT);
				for(FileIdentifier fid: fileIdentifiers)
					process.getFileIndexer().merge(fid);
			}
			process.ensureReplicaCount();
		}
	}
}
