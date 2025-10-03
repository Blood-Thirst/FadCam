class Solution {
public:
    vector<vector<int>>dp;
    int maxScore(vector<vector<int>>& grid) 
    {
        int n=grid.size(),m=grid[0].size(),ans=INT_MIN;
        dp.resize(n,vector<int>(m));
        dp[n-1][m-1]=grid[n-1][m-1];
        for(int i=m-2;i>=0;--i)
        {
            dp[n-1][i]=max(dp[n-1][i+1],grid[n-1][i+1]);
        }
        for(int i=n-2;i>=0;--i)
        {
            dp[i][m-1]=max(dp[i+1][m-1],grid[i+1][m-1]);
        }
        for(int i=n-2;i>=0;--i)
        {
            for(int j=m-2;j>=0;--j)
            {
                dp[i][j]=max({dp[i+1][j],dp[i][j+1],grid[i+1][j],grid[i][j+1]});
            }
        }
        ans=INT_MIN;
        for(int i=0;i<n;++i)
        {
            for(int j=0;j<m;++j)
            {
                // cout<<dp[i][j]<<" ";
                if(i==n-1 && j==m-1)
                {
                    continue;
                }
                ans=max(ans,dp[i][j]-grid[i][j]);
            }
            // cout<<'\n';
        }
        // for(int i=0;i<n;++i)
        // {
        //     for(int j=1;j<m;++j)
        //     {
        //         ans=max(ans,grid[i][j]-grid[i][j-1]);
        //     }
        // }
        // for(int i=0;i<m;++i)
        // {
        //     for(int j=1;j<n;++j)
        //     {
        //         ans=max(ans,grid[j][i]-grid[j-1][i]);
        //     }
        // }
        // for(int i=0;i<n;++i)
        // {
        //     for(int j=0;j<m;++j)
        //     {
        //         ans=max(ans,solve(i,j,grid));
        //     }
        // }
        return ans==INT_MIN?-1:ans;

    }
};
