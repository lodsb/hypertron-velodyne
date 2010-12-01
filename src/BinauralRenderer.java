import jass.generators.PositionData;
import jass.generators.SpatialMixer;
import processing.core.PVector;


public class BinauralRenderer {
	private PVector[] positions;
	private int sources;
	private SpatialMixer spatialMixer;
	
	public BinauralRenderer(int sources, PVector listener, PVector[] positions) {
		this.positions = positions;
		this.sources = sources;
		
		PositionData[] poses = new PositionData[sources];
		
		int i = 0;
		for(PVector pos: positions) {
			pos.set(pos.sub(pos, listener));
			
			poses[i] = new PositionData(pos.x, pos.y, pos.z);

			i++;
		}
		
		spatialMixer = new SpatialMixer(2048, this.sources, "media/datahog/work/lazerdoom/eclipse_workspace/obj2/db/CIPIC_hrtf_database/standard_hrir_database/subject_003/hrir_final.mat");
	}
}
