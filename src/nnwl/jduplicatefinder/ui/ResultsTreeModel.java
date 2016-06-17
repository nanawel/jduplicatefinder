package nnwl.jduplicatefinder.ui;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.filesystem.FileSystemInterface;
import org.apache.log4j.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTreeModel extends DefaultTreeModel {
	private static final Logger logger = Logger.getLogger(ResultsTreeModel.class);

	private static final long serialVersionUID = 7344346841047639198L;

	/**
	 * Reference path => FileResult tree node
	 */
	private TreeMap<Path, DefaultMutableTreeNode> fileResultNodes = new TreeMap<Path, DefaultMutableTreeNode>();

	/**
	 * Directory => tree node
	 */
	private TreeMap<Path, DefaultMutableTreeNode> directoryNodes = new TreeMap<Path, DefaultMutableTreeNode>();

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

		for (Map.Entry<Path, FileResult> cursor : results.entrySet()) {
			DefaultMutableTreeNode fileResultNode = this.addResultNode(cursor.getValue());
			this.fileResultNodes.put(cursor.getValue().getReferenceFile(), fileResultNode);
		}
		this.reload(this.getRoot());

		logger.debug("Tree model updated");
	}

	public DefaultMutableTreeNode getRoot() {
		return (DefaultMutableTreeNode) super.getRoot();
	}

	public DefaultMutableTreeNode getSubrootForFile(File file) {
		return this.getSubrootForPath(file.toPath());
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
		// Add reference path node if it does not exist
		DefaultMutableTreeNode parentFolderNode = this.createNodeFileTree(fileResult.getReferenceFile().getParent());
		if (parentFolderNode == null) {
			throw new IllegalStateException(fileResult.getReferenceFile().toAbsolutePath().toString());
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

	public DefaultMutableTreeNode getNodeFromPath(Path path) {
		if (this.fileResultNodes.containsKey(path)) {
			return this.fileResultNodes.get(path);
		}
		if (this.directoryNodes.containsKey(path)) {
			return this.directoryNodes.get(path);
		}
		return null;
	}

	public DefaultMutableTreeNode createNodeFileTree(Path path) {
		DefaultMutableTreeNode baseNode = this.getSubrootForPath(path);
		Path basePath = (Path) baseNode.getUserObject();
		if (basePath.equals(path)) {
			return baseNode;
		}
		DefaultMutableTreeNode childNode = null;
		for (int i = basePath.getNameCount(); i < path.getNameCount(); i++) {
			Path folderPath = basePath.getRoot().resolve(path.subpath(0, i + 1));
			childNode = this.getChildNodeFromValue(baseNode, folderPath);
			if (childNode == null) {
				childNode = new DefaultMutableTreeNode(folderPath);
				baseNode.add(childNode);
				this.directoryNodes.put(folderPath, childNode);
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
		Path referenceFile = similarityResult.getReferenceFile();

		DefaultMutableTreeNode fileResultNode = this.getNodeFromPath(referenceFile);
		return this.getChildNodeFromValue(fileResultNode, similarityResult);
	}

	public DefaultMutableTreeNode getBaseFolderNode(DefaultMutableTreeNode node) {
		boolean found = false;
		while (!found && !node.isRoot()) {
			if (node.getLevel() == 1) {
				found = true;
			} else {
				node = (DefaultMutableTreeNode) node.getParent();
			}
		}
		return node;
	}

	public int getTotalNodesCount() {
		return this.totalNodesCount;
	}

	public void removeNodeAndEmptyParents(DefaultMutableTreeNode node) {
		if (node.getParent() != null) {
			if (node.getParent().getChildCount() == 1 && !((DefaultMutableTreeNode) node.getParent()).isRoot()) {
				this.removeNodeAndEmptyParents((DefaultMutableTreeNode) node.getParent());
			} else {
				this.removeNodeFromParent(node);
				//TODO update this.totalNodesCount
			}
		}
	}

	public void removeFileNodes(Path path) {
		DefaultMutableTreeNode node = this.getNodeFromPath(path);
		if (node == null) {
			logger.warn("Cannot find specified path in the tree: " + path.toAbsolutePath());
		} else {
			this.removeFileNode(node);
		}
	}

	public void removeFileNode(DefaultMutableTreeNode node) {
		// ResultNode
		if (node.getUserObject() instanceof FileResult) {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
				SimilarityResult sr = (SimilarityResult) childNode.getUserObject();
				DefaultMutableTreeNode similarFileResultNode = this.getNodeFromPath(sr.getSimilarFile());
				if (similarFileResultNode != null) {
					for (int j = 0; j < similarFileResultNode.getChildCount(); j++) {
						DefaultMutableTreeNode similarFileResultChildNode = (DefaultMutableTreeNode) similarFileResultNode
								.getChildAt(j);
						if (((SimilarityResult) similarFileResultChildNode.getUserObject()).getSimilarFile()
								.equals(((FileResult) node.getUserObject()).getReferenceFile())) {
							this.removeNodeAndEmptyParents(similarFileResultChildNode);
						}
					}
				}
			}
			this.removeNodeAndEmptyParents(node);
		}
		// Folder
		else if (node.getUserObject() instanceof Path) {
			while (node.getChildCount() > 0) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(0);
				this.removeFileNode(childNode);
			}
		} else {
			logger.debug("Unhandled userobject found on node: " + node.getUserObject());
		}
	}

	public boolean deleteFileAndTreeNode(Path path) {
		return this.deleteFileAndTreeNode(path, false);
	}

	/**
	 * @param path
	 * @return
	 */
	public boolean deleteFileAndTreeNode(Path path, boolean recursive) {
		logger.debug("Deleting path/folder: " + path.toAbsolutePath());

		if (FileSystemInterface.getFileSystemInterface().delete(path.toFile(), recursive)) {
			this.removeFileNodes(path);
			return true;
		}
		logger.error("Could not delete path/folder: " + path.toAbsolutePath());
		return false;
	}

	public void resetResults() {
		this.getRoot().removeAllChildren();
		this.fileResultNodes.clear();
		this.totalNodesCount = 0;
		logger.debug("Results have been reset");
	}

	public List<FileResult> getFileResultsInFolder(Path folder) {
		List<FileResult> results = new ArrayList<>();
		for (Map.Entry<Path, DefaultMutableTreeNode> entry : this.fileResultNodes.entrySet()) {
			Path path = entry.getKey();
			if (path.startsWith(folder)) {
				results.add(((FileResult) entry.getValue().getUserObject()));
			}
		}
		return results;
	}
}
