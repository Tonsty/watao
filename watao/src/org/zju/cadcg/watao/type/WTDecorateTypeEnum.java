package org.zju.cadcg.watao.type;

import java.util.ArrayList;
import java.util.List;

public enum WTDecorateTypeEnum {
	DANDU{
		@Override
		public List<WTDecorator> getData() {
			// TODO Auto-generated method stub
			return super.getData();
		}
	},
	CHONGFU{
		@Override
		public List<WTDecorator> getData() {
			// TODO Auto-generated method stub
			return super.getData();
		}
	},
	HUANRAO{
		@Override
		public List<WTDecorator> getData() {
			// TODO Auto-generated method stub
			return super.getData();
		}
	}, 
	CUSTOM;

	public List<WTDecorator> getData() {
		ArrayList<WTDecorator> decorators = new ArrayList<WTDecorator>();
		for (int i = 0; i < 15; i++) {
			decorators.add(new WTDecorator());
		}
		return decorators;
	}
}
