cd java
javac -cp lib/*:. *.java
cd ..

java -cp java/lib/'*':java/ ConnectedComponents -s wikipedia_pt
java -cp java/lib/'*':java/ it.unimi.dsi.webgraph.Stats -s wikipedia_pt

java -cp java/lib/'*':java/ it.unimi.dsi.webgraph.Transform transpose wikipedia_pt wikipedia_pt_trans

for i in {1..5}
do
	java -server -Xss256K -Xms35G -XX:PretenureSizeThreshold=512M -XX:MaxNewSize=4G -XX:+UseNUMA -XX:+UseTLAB -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=00 -XX:+UseCMSInitiatingOccupancyOnly -verbose:gc -Xloggc:gc.log -cp java/lib/'*':java/ it.unimi.dsi.webgraph.algo.HyperBall -l 12 wikipedia_pt wikipedia_pt_trans > "wikipedia_pt.anf.$i"
done

java -cp java/lib/'*':java ApproximateNeighbourhoodFunctionStats wikipedia_pt.anf.*

python python/graphs.py
