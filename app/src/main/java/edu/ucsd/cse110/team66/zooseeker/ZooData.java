package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

public class ZooData {
    public static class VertexInfo {
        public static enum Kind {
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("intersection") INTERSECTION,
            @SerializedName("exhibit_group") GROUP
        }

        public String id;
        public String group_id;
        public Kind kind;
        public String name;
        public List<String> tags;
        public boolean added;
        public double lat;
        public double lng;
    }

    public static class EdgeInfo {
        public String id;
        public String street;
    }

    public static List<ZooData.VertexInfo>
            loadZooItemJSON(Context context, String path, String flag) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooData.VertexInfo>>(){}.getType();
            List<ZooData.VertexInfo> zooData = gson.fromJson(reader, type);

            // Either return all the exhibits at the zoo or all the non-exhibits
            List<ZooData.VertexInfo> selectedData = new ArrayList<>();
            for (ZooData.VertexInfo item : zooData) {
                if (flag.equals("exhibits") && item.kind == VertexInfo.Kind.EXHIBIT) {
                    selectedData.add(item); // exhibits
                } else if (flag.equals("all")) {
                    selectedData.add(item);
                }
                else if (flag.equals("groups") && item.kind == VertexInfo.Kind.GROUP) {
                    selectedData.add(item);
                }
                else if (!flag.equals("exhibits") && item.kind != VertexInfo.Kind.EXHIBIT) {
                    selectedData.add(item); // gates and intersections
                }
            }
            return selectedData;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static Map<String, ZooData.EdgeInfo> loadEdgeInfoJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooData.EdgeInfo>>() {
            }.getType();
            List<ZooData.EdgeInfo> zooData = gson.fromJson(reader, type);

            Map<String, ZooData.EdgeInfo> indexedZooData = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            return indexedZooData;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public static Graph<String, IdentifiedWeightedEdge>
            loadZooGraphJSON(Context context, String path) {
        // Create an empty graph to populate.
        Graph<String, IdentifiedWeightedEdge> g
                = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        // On Android, you would use context.getAssets().open(path) here like in Lab 5.
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            // And now we just import it!
            importer.importGraph(g, reader);
            return g;
        } catch (IOException e) {
            e.printStackTrace();
            return (Graph<String, IdentifiedWeightedEdge>) Collections.emptyMap();
        }
    }
}