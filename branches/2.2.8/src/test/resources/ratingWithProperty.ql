alias 仓储TP  物流订单.仓储TPID;
alias 物流TP  物流订单.物流TPID;
alias 包装TP  物流订单.包装TPID;
仓储费 = 100;
费用.仓储TP.仓储费 = 物流订单.重量 * 0.5 ;
费用.物流TP.运输费= 3.0; 
费用.包装TP.包装费= 物流订单.重量 * 2.5 ; 
