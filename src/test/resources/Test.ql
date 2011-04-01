import java.math.BigDecimal;
		
		BigDecimal bs = new BigDecimal("0");
		for (int i = 0; i < 26; i++){
			bs = bs.add(new BigDecimal("0.1"));
		}
		System.out.println(bs);