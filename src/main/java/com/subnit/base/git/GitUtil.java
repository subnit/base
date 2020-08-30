package com.subnit.base.git;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.subnit.base.data.FileUtil.deleteDir;

/**
 * description: git operation
 * date : create in 上午9:56 2020/8/29
 * modified by :
 *
 * @author subo
 */
public class GitUtil {


    public static String cloneGit(String userName, String password, String repo, String branch, String directory)  {
        deleteDir(new File(directory));
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(userName, password);
        try {
            Git git = Git.cloneRepository().setURI(repo)
                    .setBranch(branch)
                    .setDirectory(new File(directory))
                    .setCredentialsProvider(credentialsProvider)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return directory;
    }


    public static List<DiffEntry> getDiff(CredentialsProvider credentialsProvider, String repo, String gitBranchA, String gitBranchB, String directory) throws GitAPIException, IOException {
        deleteDir(new File(directory + "/" + gitBranchA));
        deleteDir(new File(directory + "/" + gitBranchB));
        Git gitA = Git.cloneRepository().setURI(repo)
                .setBranch(gitBranchA)
                .setDirectory(new File(directory + "/" + gitBranchA))
                .setCredentialsProvider(credentialsProvider)
                .call();
        Git gitB = Git.cloneRepository().setURI(repo)
                .setBranch(gitBranchA)
                .setDirectory(new File(directory + "/" + gitBranchB))
                .setCredentialsProvider(credentialsProvider)
                .call();
        AbstractTreeIterator oldTreeParser = prepareTreeParser(gitA.getRepository(), "refs/heads/" + gitBranchA);
        AbstractTreeIterator newTreeParser = prepareTreeParser(gitB.getRepository(), "refs/heads/" + gitBranchB);
        return gitA.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
    }

    public static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        Ref head = repository.exactRef(ref);
        if (head == null) {
            return null;
        }
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }

    }


    public static void main(String[] args) throws GitAPIException, IOException {
        String userName = "subnit@163.com";
        String password = "tiantian0971";
        String repo = "https://github.com/subnit/impactsAnalysis.git";
        String branch = "master";
        String directory = "/Users/huihui/gitTemp";


        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(userName, password);
         String gitBranchA = "master";
        String gitBranchB = "gitUtil";
        List<DiffEntry> diff = getDiff(credentialsProvider, repo, gitBranchA, gitBranchB, directory);
        System.out.println(JSONObject.toJSONString(diff));


    }


}
