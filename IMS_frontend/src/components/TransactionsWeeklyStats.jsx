import React from 'react'
import ApexCharts from 'apexcharts';
import { useEffect, useState } from 'react';
const TransactionsWeeklyStats = ({ last7DaysSale, last7DaysPurchase }) => {

const sales = last7DaysSale?.map((d) => ({
  x: new Date(d.date).toLocaleDateString('en-US', {weekday: 'short'}),
  y: Number(d.totalSales),
}))

const purchases = last7DaysPurchase?.map((d) => ({
  x: new Date(d.date).toLocaleDateString('en-US', {weekday: 'short'}),
  y: Number(d.totalPurchases),
}))

console.log(sales);
const options = {
  colors: ["#1A56DB", "#FDBA8C"],
  series: [
    { name: "Sales", data: sales },
    { name: "Purchases", data: purchases }
  ],
  xaxis: {
    type: "category", 
    labels: {
      style: { fontFamily: "Inter, sans-serif" },
    },
    axisBorder: { show: false },
    axisTicks: { show: false },
  },
  chart: {
    type: "bar",
    height: "320px",
    fontFamily: "Inter, sans-serif",
    toolbar: {
      show: false,
    },
  },
  plotOptions: {
    bar: {
      horizontal: false,
      columnWidth: "70%",
      borderRadiusApplication: "end",
      borderRadius: 8,
    },
  },
  tooltip: {
    y: {
    formatter: (val) => val.toLocaleString('en-PH', { style: 'currency', currency: 'PHP' })
  },
    shared: true,
    intersect: false,
    style: {
      fontFamily: "Inter, sans-serif",
    },
  },
  states: {
    hover: {
      filter: {
        type: "darken",
        value: 1,
      },
    },
  },
  stroke: {
    show: true,
    width: 0,
    colors: ["transparent"],
  },
  grid: {
    show: false,
    strokeDashArray: 4,
    padding: {
      left: 2,
      right: 2,
      top: -14
    },
  },
  dataLabels: {
    enabled: false,
  },
  legend: {
    show: false,
  },
  xaxis: {
    floating: false,
    labels: {
      show: true,
      style: {
        fontFamily: "Inter, sans-serif",
        cssClass: 'text-xs font-normal fill-gray-500 dark:fill-gray-400'
      }
    },
    axisBorder: {
      show: false,
    },
    axisTicks: {
      show: false,
    },
  },
  yaxis: {
    show: false,
  },
  fill: {
    opacity: 1,
  },
}

console.log(options);
const [loading, setLoading] = useState(true);
useEffect(() => {
  const chart = new ApexCharts(document.querySelector("#column-chart"), options);
  chart.render();
setLoading(false);
  return () => {
    chart.destroy(); 
  };
  
}, [sales, purchases]);


  return (
    <div>
<div className="max-w-sm w-full bg-white rounded-lg shadow-s dark:bg-gray-30 p-4 md:p-6">
  <div className="flex justify-between pb-4 mb-4 border-b border-gray-200 dark:border-gray-700">
    <div className="flex items-center">
     
      <div>
        <h5 className="leading-none text-2xl font-bold text-gray-500 pb-1">Weekly Transaction Stats</h5>
      </div>
    </div>
  </div>

{loading && (<div class="flex items-center justify-center">
  <div class="w-10 h-10 mt-20 border-4 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
</div>)}
  <div id="column-chart"></div>
    <div className="grid grid-cols-1 items-center border-gray-100 dark:border-gray-400 justify-between">
      <div className="flex justify-between items-center pt-5">
      </div>
    </div>
</div>
</div>
  )
}

export default TransactionsWeeklyStats