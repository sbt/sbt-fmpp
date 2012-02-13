object Test {
<#list 1..9 as i>
  def f${i} = sys.error("noop -> ${i}")
</#list>
}
