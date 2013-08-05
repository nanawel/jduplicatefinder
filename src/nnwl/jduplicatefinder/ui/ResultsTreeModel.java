package nnwl.jduplicatefinder.ui;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;

import org.apache.log4j.Logger;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTreeModel extends DefaultTreeModel
{
	private static final Logger logger = Logger.getLogger(ResultsTreeModel.class);

	private static final long serialVersionUID = 7344346841047639198L;

	/**
	 * Reference file => FileResult tree node
	 */
	private TreeMap<File, DefaultMutableTreeNode> fileResultNodes;
	
	private int totalNodesCount = 1;

	public ResultsTreeModel() {
		super(new DefaultMutableTreeNode("Results"));
	}

	public void setResults(Path[] roots, ResultsSet results) {
		logger.debug("Preparing to display " + results.size() + " results");

		for (int i = 0; i < roots.length; i++) {
			DefaultMutableTreeNode treeSubRootNode = new DefaultMutableTreeNode();
			treeSubRootNode.setUserObject(roots[i]);
			this.getRoot().add(treeSubRootNode);
			this.totalNodesCount++;
		}

		this.fileResultNodes = new TreeMap<File, DefaultMutableTreeNode>();
		for (Map.Entry<File, FileResult> cursor : results.entrySet()) {
			DefaultMutableTreeNode fileResultNode = this.addResultNode(cursor.getValue());
			this.fileResultNodes.put(cursor.getValue().getReferenceFile(), fileResultNode);
		}
		this.reload((DefaultMutableTreeNode) this.getRoot());

		logger.debug("Tree model updated");
	}

	public DefaultMutableTreeNode getRoot() {
		return (DefaultMutableTreeNode) super.getRoot();
	}

	public DefaultMutableTreeNode getSubrootForPath(Path path) {
		for (int i = 0; i < this.getRoot().getChildCount(); i++) {
			DefaultMutableTreeNode subRootNode = (DefaultMutableTreeNode) this.getRoot().getChildAt(i);
			if (((Path) subRootNode.getUserObject()).getRoot().equals(path.getRoot())) {
				return (DefaultMutableTreeNode) this.getRoot().getChildAt(i);
			}
		}
		throw new IllegalArgumentException("Unable to find matching root for path: " + path);
	}

	public DefaultMutableTreeNode addResultNode(FileResult fileResult) {
		// Add reference file node if it does not exist
		DefaultMutableTreeNode parentFolderNode = this.createNodeFileTreeForPath(fileResult.getReferenceFile()
				.getParentFile());
		if (parentFolderNode == null) {
			throw new IllegalStateException(fileResult.getReferenceFile().getAbsolutePath());
		}

		DefaultMutableTreeNode fileResultNode = this.getChildNodeFromValue(parentFolderNode, fileResult);
		if (fileResultNode == null) {
			fileResultNode = new DefaultMutableTreeNode(fileResult);
			parentFolderNode.add(fileResultNode);
			this.totalNodesCount++;
		}

		// Then add similarity results
		for (Map.Entry<String, SimilarityResult> sr : fileResult.getCombinedSimilarityResults().entrySet()) {
			fileResultNode.add(new DefaultMutableTreeNode(sr.getValue()));
			this.totalNodesCount++;
		}

		return fileResultNode;
	}

	public DefaultMutableTreeNode getNodeFromPath(File path) {
		if (path.isFile()) {
			return this.fileResultNodes.get(path);
		}
		return null;
	}

	public DefaultMutableTreeNode createNodeFileTreeForPath(File path) {
		return this.createNodeFileTreeForPath(path.toPath());
	}

	public DefaultMutableTreeNode createNodeFileTreeForPath(Path path) {
		DefaultMutableTreeNode baseNode = this.getSubrootForPath(path);
		Path basePath = (Path) baseNode.getUserObject();

		if (basePath.equals(path)) {
			return baseNode;
		}

		DefaultMutableTreeNode childNode = null;
		for (int i = basePath.getNameCount(); i < path.getNameCount(); i++) {
			childNode = this.getChildNodeFromValue(baseNode, path.getName(i));
			if (childNode == null) {
				childNode = new DefaultMutableTreeNode(path.getName(i));
				baseNode.add(childNode);
				this.totalNodesCount++;
			}
			if (i < path.getNameCount() - 1) {
				baseNode = childNode;
			}
		}
		return childNode;
	}

	public DefaultMutableTreeNode getChildNodeFromValue(DefaultMutableTreeNode node, Object value) {
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) node.getChildAt(i);
			if (value.equals(currentNode.getUserObject())) {
				return currentNode;
			}
		}
		return null;
	}

	public DefaultMutableTreeNode getNodeFromValue(SimilarityResult similarityResult) {
		File referenceFile = similarityResult.getReferenceFile();

		DefaultMutableTreeNode fileResultNode = this.fileResultNodes.get(referenceFile);
		return this.getChildNodeFromValue(fileResultNode, similarityResult);
	}

	public DefaultMutableTreeNode getBaseFolderNode(DefaultMutableTreeNode node) {
		boolean found = false;
		while (!found && !node.isRoot()) {
			if (node.getLevel() == 1) {
				found = true;
			}
			else {
				node = (DefaultMutableTreeNode) node.getParent();
			}
		}
		return node;
	}

	/**
	 * @param path
	 * @return The node matching the given reference file path
	 */
	public DefaultMutableTreeNode getFileResultTreeNodeFromPath(String path) {
		return this.fileResultNodes.get(path);
	}

	public int getTotalNodesCount() {
		return totalNodesCount;
	}

	public void removeNodeAndEmptyParents(DefaultMutableTreeNode node) {
		if (node.getParent() != null) {
			if (node.getParent().getChildCount() == 1 && !((DefaultMutableTreeNode) node.getParent()).isRoot()) {
				this.removeNodeAndEmptyParents((DefaultMutableTreeNode) node.getParent());
			}
			else {
				this.removeNodeFromParent(node);
				//TODO update this.totalNodesCount
			}
		}
	}

	public void removeFileNodes(File f) {
		DefaultMutableTreeNode fileResultNode = this.fileResultNodes.get(f);
		if (fileResultNode != null) {
			for (int i = 0; i < fileResultNode.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) fileResultNode.getChildAt(i);
				SimilarityResult sr = (SimilarityResult) childNode.getUserObject();
				DefaultMutableTreeNode similarFileResultNode = this.fileResultNodes.get(sr.getSimilarFile());
				if (similarFileResultNode != null) {
					for (int j = 0; j < similarFileResultNode.getChildCount(); j++) {
						DefaultMutableTreeNode similarFileResultChildNode = (DefaultMutableTreeNode) similarFileResultNode
								.getChildAt(j);
						if (((SimilarityResult) similarFileResultChildNode.getUserObject()).getSimilarFile().equals(f)) {
							this.removeNodeAndEmptyParents(similarFileResultChildNode);
						}
					}
				}
			}
			this.removeNodeAndEmptyParents(fileResultNode);
		}
	}

	public void resetResults() {
		((DefaultMutableTreeNode) this.getRoot()).removeAllChildren();
		logger.debug("Results reset");
	}
}
