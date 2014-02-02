<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>

	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant version="1.1">
			<id>praha-food-garden-kavci-hory</id>
			<name>FOOD GARDEN KAVČÍ HORY</name>
			<phone>+420 739 505 818</phone>
			<e-mail>kavci.hory@foodgarden.cz</e-mail>
			<web>http://fgkavcihory.cateringmelodie.cz</web>
			<city>Praha 4</city>
			<country>Česká republika</country>
			<address>Kavčí Hory Office Park
				Na Hřebenech II 1718/10
			</address>
			<zip>140 21</zip>

			<source time="relative" base="day" firstDayOfWeek="Po"
				timeOffset="-1" encoding="cp1250" dateFormat="d.M.y" locale="cs_CZ"
				url="http://fgkavcihory.cateringmelodie.cz/cz/samoobsluzna-restaurace/denni-menu-tisk.php" />
			<source time="relative" base="day" firstDayOfWeek="Po"
				timeOffset="-1" encoding="cp1250" dateFormat="d.M.y" locale="cs_CZ"
				url="http://fgkavcihory.cateringmelodie.cz/cz/samoobsluzna-restaurace/denni-menu-pristi-tyden.php" />

			<open>
				<term day-of-week="Po" from="8:00" to="18:00" />
				<term day-of-week="Út" from="8:00" to="18:00" />
				<term day-of-week="St" from="8:00" to="18:00" />
				<term day-of-week="Čt" from="8:00" to="18:00" />
				<term day-of-week="Pá" from="8:00" to="18:00" />

				<term day-of-week="Po" from="11:00" to="15:00" />
				<term day-of-week="Út" from="11:00" to="15:00" />
				<term day-of-week="St" from="11:00" to="15:00" />
				<term day-of-week="Čt" from="11:00" to="15:00" />
				<term day-of-week="Pá" from="11:00" to="15:00" />
			</open>

			<menu>
				<xsl:apply-templates
					select="//table[@class='tb_jidelak' and position() = 1]/tbody/tr[1]/td"
					mode="days" />
			</menu>
		</restaurant>
	</xsl:template>

	<xsl:template match="td" mode="days">
		<xsl:variable name="pos" select="position()" />
		<xsl:variable name="ref-time"
			select="substring-before(/html/body/div[@class='okno-tisk']/center/div[@class='vnitrek-tisk']/p[@class='centrovani velka']/b/text(), ' ') " />

		<xsl:apply-templates
			select="../../tr[2]/td[$pos]/p[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'1-normal'" />
			<xsl:with-param name="dish" select="'soup'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[position() &gt; 2 and position() &lt; 6]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'1-normal'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[6]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'2-vegetarian'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[7]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'3-salad'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and ( position() = 1)]/tbody/tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'4-superior'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and (position() = 2 )]/tbody/tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'5-live'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and position() = 3]/tbody/tr[2]/td[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'6-pasta'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="td|td/p">
		<xsl:param name="type" />
		<xsl:param name="dish" />
		<xsl:param name="time" />
		<xsl:param name="ref-time" />
		<meal order="{position()}" category="{$type}" dish="{$dish}"
			time="{$time}" ref-time="{$ref-time}">
			<title>
				<xsl:value-of select="." />
			</title>
			<description></description>
			<xsl:choose>
				<xsl:when test="$dish = 'soup'">
					<price>18 Kč</price>
				</xsl:when>
				<xsl:when test="$type = '4-superior'">
					<price>90 Kč</price>
				</xsl:when>
				<xsl:when test="$type = '5-live'">
					<price>110 Kč</price>
				</xsl:when>
				<xsl:when test="$type = '6-pasta'">
					<price>84 Kč</price>
				</xsl:when>
				<xsl:otherwise>
					<price>74 Kč</price>
				</xsl:otherwise>
			</xsl:choose>
		</meal>
	</xsl:template>

</xsl:stylesheet>